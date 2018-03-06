package com.mapswithme.maps.location;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mapswithme.maps.MwmApplication;
import com.mapswithme.maps.MwmActivity;

import android.hardware.SensorManager;

import com.stoke.*;
import com.stoke.types.*;
import com.stoke.util.*;
import com.stoke.eval.*;

import android.content.Context;
import android.telephony.*;

import java.util.List;

class GoogleFusedLocationProvider extends BaseLocationProvider
                               implements GoogleApiClient.ConnectionCallbacks,
                                          GoogleApiClient.OnConnectionFailedListener
{
  // Injecting machine stuff 

  private final static String TAG = GoogleFusedLocationProvider.class.getSimpleName();
  private final GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;
  private PendingResult<LocationSettingsResult> mLocationSettingsResult;
  @NonNull
  private final BaseLocationListener mListener;

  private Handler handler = null;
  private Runnable runner = null;

  int lastPriority;
  int lastInterval;
  int lastSensor;
  int lastBatch;


  int runCount = 0;

  float lastBattery = 0;
  int lastBatteryLevel = 0;

  float idealBatteryEpsilon = 0;
  int idealBatteryLevelEpsilon = 0;

  float idealBatteryDrain = 0;
  int idealBatteryLevelDrain = 0;

  int minGpsUpdate = 0;
  int maxGpsUpdate = 10000;
  int curGpsUpdate = 0;

  int curTick = 0;

  GoogleFusedLocationProvider(@NonNull LocationFixChecker locationFixChecker, TelephonyManager telephonyManager)
  {
    super(locationFixChecker);
    mGoogleApiClient = new GoogleApiClient.Builder(MwmApplication.get())
                                          .addApi(LocationServices.API)
                                          .addConnectionCallbacks(this)
                                          .addOnConnectionFailedListener(this)
                                          .build();
    mListener = new BaseLocationListener(locationFixChecker);
    mTelephonyManager = telephonyManager;
  }

  private void fixAccuracyChoice() {
    int accuracyChoice = ((Integer) AndroidUtil.getProperty("MAPSME_GPS_ACCURACY_CHOICE"));
    if (accuracyChoice == 0) {
      lastPriority = LocationRequest.PRIORITY_LOW_POWER;
    } else if (accuracyChoice == 1) {
      lastPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    } else if (accuracyChoice == 2) {
      lastPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
    } else { 
      lastPriority = LocationRequest.PRIORITY_NO_POWER;
    } 
  }

  private void fixGpsRate() {
    lastInterval = ((Integer) AndroidUtil.getProperty("MAPSME_GPS_RATE"));
  }

  private String gpsAccuracyString() {
    switch (lastPriority) {
      case LocationRequest.PRIORITY_LOW_POWER:
        return "Low Power";
      case LocationRequest.PRIORITY_HIGH_ACCURACY:
        return "High Accuracy";
      case LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY:
        return "Balance Accuracy";
      case LocationRequest.PRIORITY_NO_POWER:
        return "No Power";
      default:
        return "Unexpected Accuracy";
    }
  }

  private String sensorAccuracyString() {
    switch (lastSensor) {
      case MwmActivity.SENSOR_OFF: 
        return "Sensor Off";
      case MwmActivity.SENSOR_CALLBACK_OFF:
        return "Callbacks Off";
      case MwmActivity.SENSOR_FULL:
        return "Sensors On";
      default:
        return "Unexpected Sensor";
    }
  }

  private String batchString() {
    return String.format("%dx\n", lastBatch);
  }


  private String gpsUpdateRateString() {
    return String.format("%d", lastInterval);
  }

  private void fixSensorRate() {
    int sensorChoice = ((Integer) AndroidUtil.getProperty("MAPSME_SENSOR_RATE_CHOICE"));
    if (sensorChoice == 0) {
      lastSensor = MwmActivity.SENSOR_OFF;
    } else if (sensorChoice == 1) {
      lastSensor = MwmActivity.SENSOR_CALLBACK_OFF;
    } else {
      lastSensor = MwmActivity.SENSOR_FULL;
    } 
  }

  private void fixBatch() {
    lastBatch = ((Integer) AndroidUtil.getProperty("MAPSME_BATCH"));
  }

  protected void initConfigurationExperiment() {
    fixAccuracyChoice(); 
    fixGpsRate();
    fixSensorRate();
    fixBatch();
  }

  protected void initMachineExperiment() {
    lastPriority = KnobValT.needInteger(MwmActivity.machine.read("priority"));
    lastInterval = KnobValT.needInteger(MwmActivity.machine.read("interval"));
    lastSensor = KnobValT.needInteger(MwmActivity.machine.read("sensor-configuration"));
    lastBatch = KnobValT.needInteger(MwmActivity.machine.read("batch"));
  }

  private void dumpMapsMeSettings() {
    LogUtil.writeLogger(String.format("MAPSME_GPS_RATE: %s\n", gpsUpdateRateString()));
    LogUtil.writeLogger(String.format("MAPSME_GPS_PRIORITY: %s\n", gpsAccuracyString()));
    LogUtil.writeLogger(String.format("MAPSME_GPS_BATCH: %s\n", batchString()));
  }

  private void refreshKnobDisplay() {
    MwmActivity.currentGpsRateView.setText(String.format("GPS Rate: %s ms", gpsUpdateRateString()));
    MwmActivity.currentGpsAccuracyView.setText(String.format("GPS Accuracy: %s", gpsAccuracyString()));
    MwmActivity.currentSensorRateView.setText(String.format("Sensor Configuration: %s", sensorAccuracyString()));
    MwmActivity.currentBatchView.setText(String.format("Batch: %s", batchString()));
  }

  private TelephonyManager mTelephonyManager;

  private void dumpCellInfo() {
    List<CellInfo> cellInfos = mTelephonyManager.getAllCellInfo();
    boolean anyRegistered = false;
    if (cellInfos == null || cellInfos.size() == 0) {
      System.err.format("STOKE: No signal!");
    }
    for (CellInfo ci : cellInfos) {
      if (!ci.isRegistered()) { continue; }
      anyRegistered = true;
      CellSignalStrength signal = null;
      String radio = "";
      if (ci instanceof CellInfoCdma) {
        signal = ((CellInfoCdma) ci).getCellSignalStrength();
        radio = "CDMA";
      } else if (ci instanceof CellInfoGsm) {
        signal = ((CellInfoGsm) ci).getCellSignalStrength();
        radio = "GSM";
      } else if (ci instanceof CellInfoLte) {
        signal = ((CellInfoLte) ci).getCellSignalStrength();
        radio = "LTE";
      } else if (ci instanceof CellInfoWcdma) {
        signal = ((CellInfoWcdma) ci).getCellSignalStrength();
        radio = "WCDMA";
      } 
      System.err.format("STOKE: Signal: Info:%s dBM:%d level:%d\n", radio, signal.getDbm(), signal.getLevel());
      LogUtil.writeLogger(String.format("Signal: Info:%s dBM:%d level:%d\n", radio, signal.getDbm(), signal.getLevel()));
    } 
  }

  @Override
  protected void start()
  {
    LOGGER.d(TAG, "Google fused provider is started");
    if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())
    {
      setActive(true);
      return;
    }

    mLocationRequest = LocationRequest.create();

    if (MwmActivity.experiment != Experiment.OVERHEAD_APP) {
      MwmActivity.machine.start();
    }

    switch (MwmActivity.experiment) {
      case IGNORE:
      case OVERHEAD_APP:
        initConfigurationExperiment();
        break;
      default:
        initMachineExperiment();
        break;
    } 
    
    mLocationRequest.setPriority(lastPriority);
    mLocationRequest.setInterval(lastInterval);
    mLocationRequest.setFastestInterval(lastInterval);
    mLocationRequest.setMaxWaitTime(lastInterval * lastBatch);
    LocationHelper.INSTANCE.startSensors(lastSensor); 

    mGoogleApiClient.connect();
    setActive(true);

    final int taskInterval = ((Integer) AndroidUtil.getProperty("MAPSME_TASK_INTERVAL"));
    final int actualInterval;
    if (MwmActivity.usingSelfOptimizer) {
      actualInterval = KnobValT.needInteger(MwmActivity.machine.read("task-interval"));
    } else {
      actualInterval = taskInterval;
    }
    
    refreshKnobDisplay();

    handler = new Handler();
    runner = new Runnable() {
      @Override
      public void run() {
        if (MwmActivity.experiment != Experiment.OVERHEAD_APP) {
          MwmActivity.machine.interact();
        } else {

          //System.err.format("STOKE: Volt: %f Current: %f\n", MwmActivity.androidReward.getBatteryVoltage(), MwmActivity.androidReward.getBatteryAmps());
          System.err.format("STOKE: Tick: %d\n", MwmActivity.repeatCount);

          if (MwmActivity.repeatCount >= MwmActivity.machine.getTaskSkip() &&
              MwmActivity.repeatCount < MwmActivity.repeatTotal + MwmActivity.machine.getTaskSkip()) {

            double joules = MwmActivity.androidReward.getBatteryWatts();
            double ms = actualInterval / (double) 1000;
            joules *= ms;

            MwmActivity.androidJoules += joules;
            int count = MwmActivity.repeatCount - MwmActivity.machine.getTaskSkip();

            StringBuilder sb = new StringBuilder("ETask ");
            sb.append(count);
            sb.append("-");
            sb.append(count);
            sb.append(": Configuration:");
            sb.append(0);
            sb.append(" Energy:");
            sb.append(joules);
            sb.append(" Reward:");
            sb.append(0.0);
            sb.append(" Raw:");
            sb.append(0.0);
            sb.append(" Time:");
            sb.append(actualInterval);
            sb.append(" TotalReward:");
            sb.append(MwmActivity.androidJoules);
            sb.append("\n");

            System.err.format(sb.toString());
            LogUtil.writeLogger(sb.toString());
          } else if (MwmActivity.repeatCount >= (MwmActivity.repeatTotal + MwmActivity.machine.getTaskSkip())) {
            MwmActivity.machine.done();
          }

          MwmActivity.repeatCount++;
        }

        if (MwmActivity.experiment != Experiment.IGNORE && MwmActivity.experiment != Experiment.OVERHEAD_APP) {

          int nextPriority = KnobValT.needInteger(MwmActivity.machine.read("priority"));
          int nextInterval = KnobValT.needInteger(MwmActivity.machine.read("interval"));
          int nextSensor = KnobValT.needInteger(MwmActivity.machine.read("sensor-configuration"));
          int nextBatch = KnobValT.needInteger(MwmActivity.machine.read("batch"));

          if (lastPriority != nextPriority || lastInterval != nextInterval ||
              lastBatch != nextBatch) {
            mLocationRequest.setPriority(nextPriority);
            mLocationRequest.setInterval(nextInterval);
            mLocationRequest.setFastestInterval(nextInterval);
            mLocationRequest.setMaxWaitTime(nextInterval * nextBatch);

            lastPriority = nextPriority;
            lastInterval = nextInterval;
            lastBatch = nextBatch;

            requestLocationUpdates();
          }
          if (lastSensor != nextSensor) {
            LocationHelper.INSTANCE.stopSensors();
            LocationHelper.INSTANCE.startSensors(nextSensor);

            lastSensor = nextSensor;
          } 
        }

        
        //dumpCellInfo();

        refreshKnobDisplay();

        if (MwmActivity.experiment == Experiment.DRAIN) {
          System.err.format("STOKE: Battery:%f Level:%d\n", MwmActivity.getBattery(), MwmActivity.getBatteryLevel());
          LogUtil.writeLogger(String.format("STOKE: Battery:%f Level:%d\n", MwmActivity.getBattery(), MwmActivity.getBatteryLevel()));
        }

        int actualInterval;
        if (MwmActivity.usingSelfOptimizer) {
          actualInterval = KnobValT.needInteger(MwmActivity.machine.read("task-interval"));
        } else {
          actualInterval = taskInterval;
        }

        handler.postDelayed(this, actualInterval); // Run at half second

      }
    };
    handler.postDelayed(runner, actualInterval);
  } 

  @Override
  protected void stop()
  {
    LOGGER.d(TAG, "Google fused provider is stopped");
    if (mGoogleApiClient.isConnected())
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mListener);

    if (mLocationSettingsResult != null && !mLocationSettingsResult.isCanceled())
      mLocationSettingsResult.cancel();

    System.err.format("STOKE: We are stopping!\n");

    mGoogleApiClient.disconnect();
    setActive(false);
    if (handler != null) {
      System.err.format("STOKE: Kicking off machine stop!\n");
      handler.removeCallbacks(runner);
      MwmActivity.machine.stop();
      handler = null;
    }
  }

  @Override
  public void onConnected(Bundle bundle)
  {
    LOGGER.d(TAG, "Fused onConnected. Bundle " + bundle);
    checkSettingsAndRequestUpdates();
  }

  private void checkSettingsAndRequestUpdates()
  {
    LOGGER.d(TAG, "checkSettingsAndRequestUpdates()");
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
    builder.setAlwaysShow(true); // hides 'never' button in resolve dialog afterwards.
    mLocationSettingsResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
    mLocationSettingsResult.setResultCallback(new ResultCallback<LocationSettingsResult>()
    {
      @Override
      public void onResult(@NonNull LocationSettingsResult locationSettingsResult)
      {
        final Status status = locationSettingsResult.getStatus();
        LOGGER.d(TAG, "onResult status: " + status);
        switch (status.getStatusCode())
        {
        case LocationSettingsStatusCodes.SUCCESS:
          break;

        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
          setActive(false);
          // Location settings are not satisfied. AndroidNativeProvider should be used.
          resolveResolutionRequired();
          return;

        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
          // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
          setActive(false);
          break;
        }

        requestLocationUpdates();
      }
    });
  }

  private static void resolveResolutionRequired()
  {
    LOGGER.d(TAG, "resolveResolutionRequired()");
    LocationHelper.INSTANCE.initNativeProvider();
    LocationHelper.INSTANCE.start();
  }

  private void requestLocationUpdates()
  {
    if (!mGoogleApiClient.isConnected())
      return;

    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mListener);
    //LocationHelper.INSTANCE.startSensors();
    Location last = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (last != null)
      mListener.onLocationChanged(last);
  }

  @Override
  public void onConnectionSuspended(int i)
  {
    setActive(false);
    LOGGER.d(TAG, "Fused onConnectionSuspended. Code " + i);
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
  {
    setActive(false);
    LOGGER.d(TAG, "Fused onConnectionFailed. Fall back to native provider. ConnResult " + connectionResult);
    // TODO handle error in a smarter way
    LocationHelper.INSTANCE.initNativeProvider();
    LocationHelper.INSTANCE.start();
  }
}
