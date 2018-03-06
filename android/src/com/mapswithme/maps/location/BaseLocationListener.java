package com.mapswithme.maps.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mapswithme.util.log.Logger;
import com.mapswithme.util.log.LoggerFactory;

import com.stoke.util.*;
import com.stoke.*;

class BaseLocationListener implements LocationListener, com.google.android.gms.location.LocationListener
{
  private static final String TAG = BaseLocationListener.class.getSimpleName();
  private static final Logger LOGGER = LoggerFactory.INSTANCE.getLogger(LoggerFactory.Type.LOCATION);
  @NonNull
  private final LocationFixChecker mLocationFixChecker;

  public int acceptedCount = 0;
  public int rejectedAccuracyCount = 0;
  public int rejectedWorseCount = 0;

  BaseLocationListener(@NonNull LocationFixChecker locationFixChecker)
  {
    mLocationFixChecker = locationFixChecker;
  }

  @Override
  public void onLocationChanged(Location location)
  {
    LOGGER.d(TAG, "onLocationChanged, location = " + location);

    //System.out.format("STOKE: onLocation: %s\n", location);
    //LogUtil.writeLogger(String.format("onLocation: %s\n", location));

    if (location == null)
      return;

    //System.err.format("STOKE: Accepted:%d Acc-Reject:%d Worse-Reject:%d\n", acceptedCount, rejectedAccuracyCount, rejectedWorseCount);
    //LogUtil.writeLogger(String.format("Accepted:%d Acc-Reject:%d Worse-Reject:%d\n", acceptedCount, rejectedAccuracyCount, rejectedWorseCount));

    if (!mLocationFixChecker.isAccuracySatisfied(location)) {
      //LogUtil.writeLogger(
       //    String.format("Location with accuracy:%.2f failed accuracy satisified check\n", 
        //      location.getAccuracy())); 

      //System.out.format("STOKE: Location with accuracy:%.2f failed accuracy satisified check\n", location.getAccuracy()); 

      rejectedAccuracyCount++;

      return;
    }

    if (mLocationFixChecker.isLocationBetterThanLast(location))
    {
      LocationHelper.INSTANCE.resetMagneticField(location);
      LocationHelper.INSTANCE.onLocationUpdated(location);
      LocationHelper.INSTANCE.notifyLocationUpdated();

      //System.out.format("STOKE: Location was accepted\n");

      acceptedCount++;

    }
    else
    {
      //LogUtil.writeLogger(
       //    String.format("Location with accuracy:%.2f failed better than check\n", 
        //      location.getAccuracy())); 

      //System.out.format("STOKE: Location with accuracy:%.2f failed better than check\n", location.getAccuracy()); 

      rejectedWorseCount++;

      Location last = LocationHelper.INSTANCE.getSavedLocation();
      if (last != null)
      {
        LOGGER.d(TAG, "The new location from '" + location.getProvider()
                 + "' is worse than the last one from '" + last.getProvider() + "'");
      }
    }
  }

  @Override
  public void onProviderDisabled(String provider)
  {
    LOGGER.d(TAG, "Disabled location provider: " + provider);
  }

  @Override
  public void onProviderEnabled(String provider)
  {
    LOGGER.d(TAG, "Enabled location provider: " + provider);
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras)
  {
    LOGGER.d(TAG, "Status changed for location provider: " + provider + "; new status = " + status);
  }
}
