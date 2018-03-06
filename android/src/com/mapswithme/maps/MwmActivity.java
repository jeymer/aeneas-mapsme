package com.mapswithme.maps;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mapswithme.maps.Framework.MapObjectListener;
import com.mapswithme.maps.activity.CustomNavigateUpListener;
import com.mapswithme.maps.ads.LikesManager;
import com.mapswithme.maps.api.ParsedMwmRequest;
import com.mapswithme.maps.api.ParsedRoutingData;
import com.mapswithme.maps.api.ParsedUrlMwmRequest;
import com.mapswithme.maps.api.RoutePoint;
import com.mapswithme.maps.base.BaseMwmFragmentActivity;
import com.mapswithme.maps.base.OnBackPressListener;
import com.mapswithme.maps.bookmarks.BookmarkCategoriesActivity;
import com.mapswithme.maps.bookmarks.data.Banner;
import com.mapswithme.maps.bookmarks.data.BookmarkManager;
import com.mapswithme.maps.bookmarks.data.MapObject;
import com.mapswithme.maps.downloader.DownloaderActivity;
import com.mapswithme.maps.downloader.DownloaderFragment;
import com.mapswithme.maps.downloader.MapManager;
import com.mapswithme.maps.downloader.MigrationFragment;
import com.mapswithme.maps.downloader.OnmapDownloader;
import com.mapswithme.maps.editor.AuthDialogFragment;
import com.mapswithme.maps.editor.Editor;
import com.mapswithme.maps.editor.EditorActivity;
import com.mapswithme.maps.editor.EditorHostFragment;
import com.mapswithme.maps.editor.FeatureCategoryActivity;
import com.mapswithme.maps.editor.ReportFragment;
import com.mapswithme.maps.editor.ViralFragment;
import com.mapswithme.maps.location.CompassData;
import com.mapswithme.maps.location.LocationHelper;
import com.mapswithme.maps.news.FirstStartFragment;
import com.mapswithme.maps.news.NewsFragment;
import com.mapswithme.maps.routing.NavigationController;
import com.mapswithme.maps.routing.RoutingController;
import com.mapswithme.maps.routing.RoutingPlanController;
import com.mapswithme.maps.routing.RoutingPlanFragment;
import com.mapswithme.maps.routing.RoutingPlanInplaceController;
import com.mapswithme.maps.search.FloatingSearchToolbarController;
import com.mapswithme.maps.search.HotelsFilter;
import com.mapswithme.maps.search.HotelsFilterView;
import com.mapswithme.maps.search.NativeSearchListener;
import com.mapswithme.maps.search.SearchActivity;
import com.mapswithme.maps.search.SearchEngine;
import com.mapswithme.maps.search.SearchFilterController;
import com.mapswithme.maps.search.SearchFragment;
import com.mapswithme.maps.search.SearchResult;
import com.mapswithme.maps.settings.SettingsActivity;
import com.mapswithme.maps.settings.StoragePathManager;
import com.mapswithme.maps.settings.UnitLocale;
import com.mapswithme.maps.sound.TtsPlayer;
import com.mapswithme.maps.traffic.TrafficManager;
import com.mapswithme.maps.traffic.widget.TrafficButton;
import com.mapswithme.maps.traffic.widget.TrafficButtonController;
import com.mapswithme.maps.uber.Uber;
import com.mapswithme.maps.uber.UberInfo;
import com.mapswithme.maps.widget.FadeView;
import com.mapswithme.maps.widget.menu.BaseMenu;
import com.mapswithme.maps.widget.menu.MainMenu;
import com.mapswithme.maps.widget.menu.MyPositionButton;
import com.mapswithme.maps.widget.placepage.BasePlacePageAnimationController;
import com.mapswithme.maps.widget.placepage.PlacePageView;
import com.mapswithme.maps.widget.placepage.PlacePageView.State;
import com.mapswithme.util.Animations;
import com.mapswithme.util.BottomSheetHelper;
import com.mapswithme.util.InputUtils;
import com.mapswithme.util.ThemeUtils;
import com.mapswithme.util.UiUtils;
import com.mapswithme.util.Utils;
import com.mapswithme.util.concurrency.UiThread;
import com.mapswithme.util.sharing.ShareOption;
import com.mapswithme.util.sharing.SharingHelper;
import com.mapswithme.util.statistics.AlohaHelper;
import com.mapswithme.util.statistics.Statistics;

import java.io.Serializable;
import java.util.Stack;

import com.google.android.gms.location.LocationRequest;

import com.stoke.*;
import com.stoke.types.*;
import com.stoke.util.*;
import com.stoke.eval.*;

import android.telephony.*;

import android.hardware.SensorManager;

public class MwmActivity extends BaseMwmFragmentActivity
                      implements MapObjectListener,
                                 View.OnTouchListener,
                                 BasePlacePageAnimationController.OnVisibilityChangedListener,
                                 BasePlacePageAnimationController.OnAnimationListener,
                                 OnClickListener,
                                 MapFragment.MapRenderingListener,
                                 CustomNavigateUpListener,
                                 RoutingController.Container,
                                 LocationHelper.UiCallback,
                                 RoutingPlanController.OnToggleListener,
                                 RoutingPlanController.SearchPoiTransitionListener,
                                 FloatingSearchToolbarController.VisibilityListener,
                                 NativeSearchListener
{
  public static final String EXTRA_TASK = "map_task";
  private static final String EXTRA_CONSUMED = "mwm.extra.intent.processed";
  private static final String EXTRA_UPDATE_COUNTRIES = ".extra.update.countries";

  private static final String[] DOCKED_FRAGMENTS = { SearchFragment.class.getName(),
                                                     DownloaderFragment.class.getName(),
                                                     MigrationFragment.class.getName(),
                                                     RoutingPlanFragment.class.getName(),
                                                     EditorHostFragment.class.getName(),
                                                     ReportFragment.class.getName() };
  // Instance state
  private static final String STATE_PP = "PpState";
  private static final String STATE_MAP_OBJECT = "MapObject";
  private static final String EXTRA_LOCATION_DIALOG_IS_ANNOYING = "LOCATION_DIALOG_IS_ANNOYING";

  // Map tasks that we run AFTER rendering initialized
  private final Stack<MapTask> mTasks = new Stack<>();
  private final StoragePathManager mPathManager = new StoragePathManager();

  private MapFragment mMapFragment;
  private PlacePageView mPlacePage;

  private RoutingPlanInplaceController mRoutingPlanInplaceController;
  @Nullable
  private NavigationController mNavigationController;

  private MainMenu mMainMenu;

  private PanelAnimator mPanelAnimator;
  private OnmapDownloader mOnmapDownloader;

  private FadeView mFadeView;

  private MyPositionButton mNavMyPosition;
  private TrafficButton mTraffic;
  @Nullable
  private NavigationButtonsAnimationController mNavAnimationController;
  @Nullable
  private TrafficButtonController mTrafficButtonController;

  private View mPositionChooser;

  private ViewGroup mRootView;

  @Nullable
  private SearchFilterController mFilterController;

  private boolean mIsFragmentContainer;
  private boolean mIsFullscreen;
  private boolean mIsFullscreenAnimating;
  private boolean mIsAppearMenuLater;

  private FloatingSearchToolbarController mSearchController;

  // The first launch of application ever - onboarding screen will be shown.
  private boolean mFirstStart;
  private boolean mPlacePageRestored;

  private boolean mLocationErrorDialogAnnoying = false;
  @Nullable
  private Dialog mLocationErrorDialog;

  private static IntentFilter batFilt = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
  private static Context context = null;

  public static float getBattery() {
    Intent batteryStatus = context.registerReceiver(null, batFilt);
    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

    float batteryPct = level / (float)scale;
    return batteryPct;
  } 

  public static int getBatteryLevel() {
    Intent batteryStatus = context.registerReceiver(null, batFilt);
    int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    return level;
  }

  public static int getBatteryScale() {
    Intent batteryStatus = context.registerReceiver(null, batFilt);
    int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    return scale;
  }

  public static double getBatteryVoltage() {
    Intent batteryStatus = context.registerReceiver(null, batFilt);
    int mv = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
    return (mv * 0.001);
  }

  public static double getBatteryAmps() {
    BatteryManager mBatteryManager = (BatteryManager)context.getSystemService(Context.BATTERY_SERVICE);
    return (-1 * mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) * 0.000001); 
  } 

  public static double getBatteryWatts() {
    return getBatteryVoltage() * getBatteryAmps();
  }

  @NonNull
  private final OnClickListener mOnMyPositionClickListener = new OnClickListener()
  {
    @Override
    public void onClick(View v)
    {
      mLocationErrorDialogAnnoying = false;
      LocationHelper.INSTANCE.switchToNextMode();
      LocationHelper.INSTANCE.restart();
      Statistics.INSTANCE.trackEvent(Statistics.EventName.TOOLBAR_MY_POSITION);
      AlohaHelper.logClick(AlohaHelper.TOOLBAR_MY_POSITION);
    }
  };

  public interface LeftAnimationTrackListener
  {
    void onTrackStarted(boolean collapsed);

    void onTrackFinished(boolean collapsed);

    void onTrackLeftAnimation(float offset);
  }

  public interface VisibleRectListener
  {
    void onVisibleRectChanged(Rect rect);
  }

  class VisibleRectMeasurer implements View.OnLayoutChangeListener
  {
    private VisibleRectListener m_listener;
    private Rect mScreenFullRect = null;
    private Rect mLastVisibleRect = null;
    private boolean mPlacePageVisible = false;

    public VisibleRectMeasurer(VisibleRectListener listener)
    {
      m_listener = listener;
    }

    void setPlacePageVisible(boolean visible)
    {
      int orientation = MwmActivity.this.getResources().getConfiguration().orientation;
      if(orientation == Configuration.ORIENTATION_LANDSCAPE)
      {
        mPlacePageVisible = visible;
        recalculateVisibleRect(mScreenFullRect);
      }
    }

    void setPreviewVisible(boolean visible)
    {
      int orientation = MwmActivity.this.getResources().getConfiguration().orientation;
      if(orientation == Configuration.ORIENTATION_PORTRAIT)
      {
        mPlacePageVisible = visible;
        recalculateVisibleRect(mScreenFullRect);
      }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom)
    {
      mScreenFullRect = new Rect(left, top, right, bottom);
      if (mPlacePageVisible && mPlacePage.GetPreview().getVisibility() != View.VISIBLE)
        mPlacePageVisible = false;
      recalculateVisibleRect(mScreenFullRect);
    }

    private void recalculateVisibleRect(Rect r)
    {
      if (r == null)
        return;

      int orientation = MwmActivity.this.getResources().getConfiguration().orientation;

      Rect rect = new Rect(r.left, r.top, r.right, r.bottom);
      if (mPlacePageVisible)
      {
        int[] loc = new int[2];
        mPlacePage.GetPreview().getLocationOnScreen(loc);

        if(orientation == Configuration.ORIENTATION_PORTRAIT)
          rect.bottom = loc[1];
        else
          rect.left = mPlacePage.GetPreview().getWidth() + loc[0];
      }

      if (mLastVisibleRect == null || !mLastVisibleRect.equals(rect))
      {
        mLastVisibleRect = new Rect(rect.left, rect.top, rect.right, rect.bottom);
        if (m_listener != null)
          m_listener.onVisibleRectChanged(rect);
      }
    }
  }

  private VisibleRectMeasurer mVisibleRectMeasurer;

  public static Intent createShowMapIntent(Context context, String countryId, boolean doAutoDownload)
  {
    return new Intent(context, DownloadResourcesActivity.class)
               .putExtra(DownloadResourcesActivity.EXTRA_COUNTRY, countryId)
               .putExtra(DownloadResourcesActivity.EXTRA_AUTODOWNLOAD, doAutoDownload);
  }

  public static Intent createUpdateMapsIntent()
  {
    return new Intent(MwmApplication.get(), MwmActivity.class)
               .putExtra(EXTRA_UPDATE_COUNTRIES, true);
  }

  @Override
  public void onRenderingInitialized()
  {
    checkMeasurementSystem();
    checkKitkatMigrationMove();

    LocationHelper.INSTANCE.attach(this, mTelephonyManager);
    runTasks();
  }

  @Override
  public void onRenderingRestored()
  {
    runTasks();
  }

  private void runTasks()
  {
    while (!mTasks.isEmpty())
      mTasks.pop().run(this);
  }

  private static void checkMeasurementSystem()
  {
    UnitLocale.initializeCurrentUnits();
  }

  private void checkKitkatMigrationMove()
  {
    mPathManager.checkKitkatMigration(this);
  }

  @Override
  protected int getFragmentContentResId()
  {
    return (mIsFragmentContainer ? R.id.fragment_container
                                 : super.getFragmentContentResId());
  }

  @Nullable
  Fragment getFragment(Class<? extends Fragment> clazz)
  {
    if (!mIsFragmentContainer)
      throw new IllegalStateException("Must be called for tablets only!");

    return getSupportFragmentManager().findFragmentByTag(clazz.getName());
  }

  void replaceFragmentInternal(Class<? extends Fragment> fragmentClass, Bundle args)
  {
    super.replaceFragment(fragmentClass, args, null);
  }

  @Override
  public void replaceFragment(@NonNull Class<? extends Fragment> fragmentClass, @Nullable Bundle args, @Nullable Runnable completionListener)
  {
    if (mPanelAnimator.isVisible() && getFragment(fragmentClass) != null)
    {
      if (completionListener != null)
        completionListener.run();
      return;
    }

    mPanelAnimator.show(fragmentClass, args, completionListener);
  }

  private void showBookmarks()
  {
    startActivity(new Intent(this, BookmarkCategoriesActivity.class));
  }

  public void showSearch(String query)
  {
    if (mIsFragmentContainer)
    {
      mSearchController.hide();

      final Bundle args = new Bundle();
      args.putString(SearchActivity.EXTRA_QUERY, query);
      if (mFilterController != null)
        args.putParcelable(SearchActivity.EXTRA_HOTELS_FILTER, mFilterController.getFilter());
      replaceFragment(SearchFragment.class, args, null);
    }
    else
    {
      SearchActivity.start(this, query, mFilterController != null ? mFilterController.getFilter() : null);
    }
  }

  public void showEditor()
  {
    // TODO(yunikkk) think about refactoring. It probably should be called in editor.
    Editor.nativeStartEdit();
    Statistics.INSTANCE.trackEditorLaunch(false);
    if (mIsFragmentContainer)
      replaceFragment(EditorHostFragment.class, null, null);
    else
      EditorActivity.start(this);
  }

  private void shareMyLocation()
  {
    final Location loc = LocationHelper.INSTANCE.getSavedLocation();
    if (loc != null)
    {
      final String geoUrl = Framework.nativeGetGe0Url(loc.getLatitude(), loc.getLongitude(), Framework.nativeGetDrawScale(), "");
      final String httpUrl = Framework.getHttpGe0Url(loc.getLatitude(), loc.getLongitude(), Framework.nativeGetDrawScale(), "");
      final String body = getString(R.string.my_position_share_sms, geoUrl, httpUrl);
      ShareOption.ANY.share(this, body);
      return;
    }

    new AlertDialog.Builder(MwmActivity.this)
        .setMessage(R.string.unknown_current_position)
        .setCancelable(true)
        .setPositiveButton(android.R.string.ok, null)
        .show();
  }

  @Override
  public void showDownloader(boolean openDownloaded)
  {
    if (RoutingController.get().checkMigration(this))
      return;

    final Bundle args = new Bundle();
    args.putBoolean(DownloaderActivity.EXTRA_OPEN_DOWNLOADED, openDownloaded);
    if (mIsFragmentContainer)
    {
      SearchEngine.cancelSearch();
      mSearchController.refreshToolbar();
      replaceFragment(MapManager.nativeIsLegacyMode() ? MigrationFragment.class : DownloaderFragment.class, args, null);
    }
    else
    {
      startActivity(new Intent(this, DownloaderActivity.class).putExtras(args));
    }
  }

  @Override
  @StyleRes
  public int getThemeResourceId(@NonNull String theme)
  {
    if (ThemeUtils.isDefaultTheme(theme))
      return R.style.MwmTheme_MainActivity;

    if (ThemeUtils.isNightTheme(theme))
      return R.style.MwmTheme_Night_MainActivity;

    return super.getThemeResourceId(theme);
  }

  /* STOKE */
  public static AeneasMachine machine = null; 
  public static Recording locationAccuracyRecording = null;
  public static Boolean usingSelfOptimizer = false;

  public static Reward androidReward = null;
  public static double androidJoules = 0.0;
  public static int repeatCount = 0;
  public static int repeatTotal = 0;

  public static TextView currentGpsRateView = null;
  public static TextView currentGpsAccuracyView = null;
  public static TextView currentSensorRateView = null;
  public static TextView currentBatchView = null;
  public static Experiment experiment = null;
  public static SamplingPolicy samplingPolicy = null;

  private TelephonyManager mTelephonyManager;
  
  public static final int SENSOR_FULL  = 0;
  public static final int SENSOR_OFF   = 1;
  public static final int SENSOR_CALLBACK_OFF = 2;

  public static double minLocationAcc = 0.0;

  @SuppressLint("InlinedApi")
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState)
  {
    if (machine == null) {
      //System.out.format("Compile!\n");
      //System.out.format("Compile-2!\n");
      context = getApplicationContext();

      experiment = Experiment.toExperiment((String) AndroidUtil.getProperty("STOKE_EXPERIMENT"));

      Knob priorityKnob = null;
      Knob updateKnob = null;
      Knob sensorKnob = null;
      Knob batchKnob = null;

      switch (experiment) {
        case HARDWARE_SOFTWARE:
        case OFFLINE_COLLECT:
        case MACHINE:
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new DiscreteKnob("interval", KnobValT.haveIntegers(10000, 1000, 500));
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(5,1));
          //batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(1));
          break;
        case IGNORE:
        case OVERHEAD_APP:
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new DiscreteKnob("interval", KnobValT.haveIntegers(30000));
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(1));
          break;
        case OPTIMIZER:
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new DiscreteKnob("interval", KnobValT.haveIntegers(30000, 1000, 500));
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(1));
          break;
        case CONTINUOUS:
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new InferredKnob("interval", 10000, 1000, 5);
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(1));
          break;
        case DRAIN:
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new DiscreteKnob("interval", KnobValT.haveIntegers(500));
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(1));
          break;
        case CONSTRAINED:
          //priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_LOW_POWER,LocationRequest.PRIORITY_HIGH_ACCURACY));
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new DiscreteKnob("interval", KnobValT.haveIntegers(10000, 7750, 5500, 3500, 1000));
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(1));
          /*
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new DiscreteKnob("interval", KnobValT.haveIntegers(10000,1000));
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(5,1));
          */
          break;
        case CONSTRAINED2:
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_LOW_POWER,LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new DiscreteKnob("interval", KnobValT.haveIntegers(10000, 7750, 5500, 3500, 1000));
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(5,1));
          break;
        case OVERHEAD:
        case OVERHEAD2:
          priorityKnob = new DiscreteKnob("priority", KnobValT.haveIntegers(LocationRequest.PRIORITY_LOW_POWER, LocationRequest.PRIORITY_HIGH_ACCURACY));
          updateKnob = new DiscreteKnob("interval", KnobValT.haveIntegers(10000, 1000, 500));
          sensorKnob = new DiscreteKnob("sensor-configuration", KnobValT.haveIntegers(SENSOR_OFF));
          batchKnob = new DiscreteKnob("batch", KnobValT.haveIntegers(5,1));
          break;

        default:
          throw new RuntimeException("Expected OFFLINE_COLLECT or BINARY experiment"); 
      }

      SamplingPolicy samplingPolicy = SamplingPolicy.toSamplingPolicy((String) AndroidUtil.getProperty("STOKE_SAMPLING_POLICY"));
      StochasticPolicyType stochasticPolicy = StochasticPolicyType.toStochasticPolicy(((String) AndroidUtil.getProperty("STOKE_STOCHASTIC_POLICY")));
      ConfigurationSetPolicy configPolicy = ConfigurationSetPolicy.toConfigurationSetPolicy(((String) AndroidUtil.getProperty("STOKE_CONFIGURATION_SET_POLICY")));

      locationAccuracyRecording = new Recording("location_accuracy", KnobValType.DOUBLE);

      if (AndroidUtil.getProperty("STOKE_NUM_SELF_OPTIMIZE_TICKS") != null) {
        MwmActivity.usingSelfOptimizer = true;
      }

      Constraint constraint = null;

      if (configPolicy == ConfigurationSetPolicy.CONTINUOUS_SET || experiment == Experiment.CONSTRAINED
          || experiment == Experiment.CONSTRAINED2) {
        MwmActivity.minLocationAcc = ((Double) AndroidUtil.getProperty("MAPSME_MINLOCATION"));
        constraint = new Constraint(new Recording("joule", KnobValType.DOUBLE), KnobValT.haveDouble(MwmActivity.minLocationAcc), true);
      } 

      final double sla;
      if (constraint != null) {
        sla = MwmActivity.minLocationAcc;
      } else {
        sla = 0.0;
      }

      String rewardType = (String) AndroidUtil.getProperty("STOKE_REWARD");
      if (rewardType.equals("BATTERY")) {
        MwmActivity.androidReward = new Reward(context){
          @Override 
          public double valuate() {
            double e = this.perInteractionEnergy();
            if (MwmActivity.experiment == Experiment.MACHINE) {
              return e;
            } else {
              return this.batteryRate(e);
            }
          }
          @Override
          public double SLA() {
            return sla;
          }
        };
      } else if (rewardType.equals("LOCATION")) {
        MwmActivity.androidReward = new Reward(context){
          @Override 
          public double valuate() {
            double avg = LocationHelper.INSTANCE._summedAcc / (double) LocationHelper.INSTANCE._locationCount;
            LocationHelper.INSTANCE.clearAcc();
            return avg;
          }
          @Override
          public double SLA() {
            return sla;
          }
        };
      } else {
        throw new RuntimeException("STOKE: Undefined reward type (BATTERY or LOCATION");
      }

      machine = new AeneasMachine(
        new Knob[]{updateKnob, priorityKnob, sensorKnob, batchKnob},
        new Recording[]{locationAccuracyRecording},
        constraint,
        stochasticPolicy,
        samplingPolicy,
        MwmActivity.androidReward, 
        experiment,
        MwmActivity.usingSelfOptimizer);

      int taskSkip = (Integer) AndroidUtil.getProperty("STOKE_NUM_TASK_SKIP");
      machine.setTaskSkip(taskSkip);

      int taskDelay = (Integer) AndroidUtil.getProperty("STOKE_NUM_TASK_DELAY");
      machine.setTaskDelay(taskDelay);

      int samples = (Integer) AndroidUtil.getProperty("STOKE_NUM_TASK_SAMPLES");
      machine.setNumTaskSamples(samples);

      if (experiment == Experiment.HARDWARE_SOFTWARE ||
          experiment == Experiment.OFFLINE_COLLECT) {

      } else if (experiment == Experiment.MACHINE || experiment == Experiment.IGNORE || experiment == Experiment.OPTIMIZER ||
                 experiment == Experiment.CONTINUOUS || experiment == Experiment.CONSTRAINED || experiment == Experiment.CONSTRAINED2 ||
                 experiment == Experiment.OVERHEAD || experiment == Experiment.OVERHEAD2 || experiment == Experiment.OVERHEAD_APP) {
        int repeatTotal = (Integer) AndroidUtil.getProperty("STOKE_NUM_REPEAT_TOTAL");
        int repeatNumTasks = (Integer) AndroidUtil.getProperty("STOKE_NUM_REPEAT_TASK_RESET");

        MwmActivity.repeatTotal = repeatNumTasks;

        Integer selfOptimizeTicks = (Integer) AndroidUtil.getProperty("STOKE_NUM_SELF_OPTIMIZE_TICKS");
        if (selfOptimizeTicks != null) {
          machine.setSelfOptimizeTicks(selfOptimizeTicks);
        }

        Integer selfOptimizeRounds = (Integer) AndroidUtil.getProperty("STOKE_NUM_SELF_OPTIMIZE_ROUNDS");
        if (selfOptimizeRounds != null) {
          machine.setSelfOptimizeRounds(selfOptimizeRounds);
        }


        machine.setRepeatTotal(repeatTotal);
        machine.setRepeatTaskReset(repeatNumTasks);

        machine.dumpIntervals();
      }

      if (configPolicy == ConfigurationSetPolicy.CONTINUOUS_SET) {
        int continuousSteps = (Integer) AndroidUtil.getProperty("STOKE_CONTINUOUS_STEPS");
        machine.setContinuousSteps(continuousSteps);

        int continuousRounds = (Integer) AndroidUtil.getProperty("STOKE_CONTINUOUS_ROUNDS");
        machine.setContinuousRounds(continuousRounds); 

        machine.setContinuousKnobName("interval");
      }

      mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
    }

    super.onCreate(savedInstanceState);

    if (savedInstanceState != null)
      mLocationErrorDialogAnnoying = savedInstanceState.getBoolean(EXTRA_LOCATION_DIALOG_IS_ANNOYING);

    mIsFragmentContainer = getResources().getBoolean(R.bool.tabletLayout);

    if (!mIsFragmentContainer && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP))
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    setContentView(R.layout.activity_map);
    initViews();

    Statistics.INSTANCE.trackConnectionState();

    Framework.nativeSetMapObjectListener(this);

    mSearchController = new FloatingSearchToolbarController(this);
    mSearchController.setVisibilityListener(this);
    processIntent(getIntent());
    SharingHelper.prepare();

    SearchEngine.INSTANCE.addListener(this);

    //TODO: uncomment after correct visible rect calculation.
    //mVisibleRectMeasurer = new VisibleRectMeasurer(new VisibleRectListener() {
    //  @Override
    //  public void onVisibleRectChanged(Rect rect) {
    //    Framework.nativeSetVisibleRect(rect.left, rect.top, rect.right, rect.bottom);
    //  }
    //});
    //getWindow().getDecorView().addOnLayoutChangeListener(mVisibleRectMeasurer);

    final float startBat = getBattery();
    LogUtil.writeLogger(String.format("STOKE: Start Bat: %f\n", startBat));


    BroadcastReceiver recv = new BroadcastReceiver() {
      @Override
      public void onReceive(Context arg0, Intent arg1) {
        float endBat = getBattery();
        LogUtil.writeLogger(String.format("STOKE: End Bat: %f\n", endBat));
        LogUtil.writeLogger(String.format("STOKE: Usage Bat: %f\n", startBat - endBat));
        machine.done();
      }
    };

    this.registerReceiver(recv, new IntentFilter("com.stoke.EXIT"));
    this.registerReceiver(recv, new IntentFilter("android.intent.action.BATTERY_LOW"));
  }

  private void initViews()
  {
    initMap();
    initNavigationButtons();

    mPlacePage = (PlacePageView) findViewById(R.id.info_box);
    mPlacePage.setOnVisibilityChangedListener(this);
    mPlacePage.setOnAnimationListener(this);

    if (!mIsFragmentContainer)
    {
      mRoutingPlanInplaceController = new RoutingPlanInplaceController(this);
      mRoutingPlanInplaceController.setOnToggleListener(this);
      mRoutingPlanInplaceController.setPoiTransitionListener(this);
      removeCurrentFragment(false);
    }

    mNavigationController = new NavigationController(this);
    initMainMenu();
    initOnmapDownloader();
    initPositionChooser();
    initFilterViews();
  }

  private void initFilterViews()
  {
    HotelsFilterView hotelsFilterView = (HotelsFilterView) findViewById(R.id.hotels_filter);
    View frame = findViewById(R.id.filter_frame);
    if (frame != null && hotelsFilterView != null)
    {
      mFilterController = new SearchFilterController(
          frame, hotelsFilterView, new SearchFilterController.DefaultFilterListener()
      {
        @Override
        public void onViewClick()
        {
          showSearch(mSearchController.getQuery());
        }

        @Override
        public void onFilterClear()
        {
          runSearch();
        }

        @Override
        public void onFilterDone()
        {
          runSearch();
        }
      }, R.string.search_in_table);
    }
  }

  private void runSearch()
  {
    SearchEngine.searchInteractive(mSearchController.getQuery(), System.nanoTime(),
                                   false /* isMapAndTable */,
                                   mFilterController != null ? mFilterController.getFilter() : null);
    SearchEngine.showAllResults(mSearchController.getQuery());
  }

  private void initPositionChooser()
  {
    mPositionChooser = findViewById(R.id.position_chooser);
    final Toolbar toolbar = (Toolbar) mPositionChooser.findViewById(R.id.toolbar_position_chooser);
    UiUtils.extendViewWithStatusBar(toolbar);
    UiUtils.showHomeUpButton(toolbar);
    toolbar.setNavigationOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        hidePositionChooser();
      }
    });
    mPositionChooser.findViewById(R.id.done).setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        Statistics.INSTANCE.trackEditorLaunch(true);
        hidePositionChooser();
        if (Framework.nativeIsDownloadedMapAtScreenCenter())
          startActivity(new Intent(MwmActivity.this, FeatureCategoryActivity.class));
        else
          UiUtils.showAlertDialog(MwmActivity.this, R.string.message_invalid_feature_position);
      }
    });
    UiUtils.hide(mPositionChooser);
  }

  public void showPositionChooser(boolean isBusiness, boolean applyPosition)
  {
    UiUtils.show(mPositionChooser);
    setFullscreen(true);
    Framework.nativeTurnOnChoosePositionMode(isBusiness, applyPosition);
    closePlacePage();
    mSearchController.hide();
  }

  private void hidePositionChooser()
  {
    UiUtils.hide(mPositionChooser);
    Framework.nativeTurnOffChoosePositionMode();
    setFullscreen(false);
  }

  private void initMap()
  {
    mFadeView = (FadeView) findViewById(R.id.fade_view);
    mFadeView.setListener(new FadeView.Listener()
    {
      @Override
      public boolean onTouch()
      {
        return getCurrentMenu().close(true);
      }
    });

    mMapFragment = (MapFragment) getSupportFragmentManager().findFragmentByTag(MapFragment.class.getName());
    if (mMapFragment == null)
    {
      mMapFragment = (MapFragment) MapFragment.instantiate(this, MapFragment.class.getName(), null);
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.map_fragment_container, mMapFragment, MapFragment.class.getName())
          .commit();
    }

    View container = findViewById(R.id.map_fragment_container);
    container.setOnTouchListener(this);
    mRootView = (ViewGroup) container.getParent();
  }

  private void initNavigationButtons()
  {
    View frame = findViewById(R.id.navigation_buttons);
    View zoomIn = frame.findViewById(R.id.nav_zoom_in);
    zoomIn.setOnClickListener(this);
    View zoomOut = frame.findViewById(R.id.nav_zoom_out);
    zoomOut.setOnClickListener(this);
    View myPosition = frame.findViewById(R.id.my_position);
    mNavMyPosition = new MyPositionButton(myPosition, mOnMyPositionClickListener);
    ImageButton traffic = (ImageButton) frame.findViewById(R.id.traffic);
    mTraffic = new TrafficButton(this, traffic);
    mTrafficButtonController = new TrafficButtonController(mTraffic, this);
    mNavAnimationController = new NavigationButtonsAnimationController(
        zoomIn, zoomOut, myPosition, getWindow().getDecorView().getRootView());

    View feedbackPlus = frame.findViewById(R.id.feedback_plus);
    feedbackPlus.setOnClickListener(this);
    View feedbackMinus = frame.findViewById(R.id.feedback_minus);
    feedbackMinus.setOnClickListener(this);

    currentGpsRateView = (TextView) findViewById(R.id.current_gps_rate);
    currentGpsAccuracyView = (TextView) findViewById(R.id.current_gps_accuracy);
    currentSensorRateView = (TextView) findViewById(R.id.current_sensor_rate);
    currentBatchView = (TextView) findViewById(R.id.current_batch);

  }

  public boolean closePlacePage()
  {
    if (mPlacePage.isHidden())
      return false;

    mPlacePage.hide();
    Framework.nativeDeactivatePopup();
    return true;
  }

  public boolean closeSidePanel()
  {
    if (interceptBackPress())
      return true;

    if (removeCurrentFragment(true))
    {
      InputUtils.hideKeyboard(mFadeView);
      mFadeView.fadeOut();
      return true;
    }

    return false;
  }

  private void closeAllFloatingPanels()
  {
    if (!mIsFragmentContainer)
      return;

    closePlacePage();
    if (removeCurrentFragment(true))
    {
      InputUtils.hideKeyboard(mFadeView);
      mFadeView.fadeOut();
    }
  }

  public void closeMenu(String statEvent, String alohaStatEvent, @Nullable Runnable procAfterClose)
  {
    Statistics.INSTANCE.trackEvent(statEvent);
    AlohaHelper.logClick(alohaStatEvent);

    mFadeView.fadeOut();
    mMainMenu.close(true, procAfterClose);
  }

  private boolean closePositionChooser()
  {
    if (UiUtils.isVisible(mPositionChooser))
    {
      hidePositionChooser();
      return true;
    }

    return false;
  }

  public void startLocationToPoint(String statisticsEvent, String alohaEvent, final @Nullable MapObject endPoint)
  {
    closeMenu(statisticsEvent, alohaEvent, new Runnable()
    {
      @Override
      public void run()
      {
        RoutingController.get().prepare(endPoint);

        if (mPlacePage.isDocked() || !mPlacePage.isFloating())
          closePlacePage();
      }
    });
  }

  private void toggleMenu()
  {
    getCurrentMenu().toggle(true);
    refreshFade();
  }

  public void refreshFade()
  {
    if (getCurrentMenu().isOpen())
      mFadeView.fadeIn();
    else
      mFadeView.fadeOut();
  }

  private void initMainMenu()
  {
    mMainMenu = new MainMenu(findViewById(R.id.menu_frame), new BaseMenu.ItemClickListener<MainMenu.Item>()
    {
      @Override
      public void onItemClick(MainMenu.Item item)
      {
        if (mIsFullscreenAnimating)
          return;

        switch (item)
        {
        case TOGGLE:
          if (!mMainMenu.isOpen())
          {
            if (mPlacePage.isDocked() && closePlacePage())
              return;

            if (closeSidePanel())
              return;
          }

          Statistics.INSTANCE.trackEvent(Statistics.EventName.TOOLBAR_MENU);
          AlohaHelper.logClick(AlohaHelper.TOOLBAR_MENU);
          toggleMenu();
          break;

        case ADD_PLACE:
          closePlacePage();
          if (mIsFragmentContainer)
            closeSidePanel();
          closeMenu(Statistics.EventName.MENU_ADD_PLACE, AlohaHelper.MENU_ADD_PLACE, new Runnable()
          {
            @Override
            public void run()
            {
              Statistics.INSTANCE.trackEvent(Statistics.EventName.EDITOR_ADD_CLICK,
                                             Statistics.params().add(Statistics.EventParam.FROM, "main_menu"));
              showPositionChooser(false, false);
            }
          });
          break;

        case SEARCH:
          RoutingController.get().cancel();
          closeMenu(Statistics.EventName.TOOLBAR_SEARCH, AlohaHelper.TOOLBAR_SEARCH, new Runnable()
          {
            @Override
            public void run()
            {
              showSearch(mSearchController.getQuery());
            }
          });
          break;

        case P2P:
          startLocationToPoint(Statistics.EventName.MENU_P2P, AlohaHelper.MENU_POINT2POINT, null);
          break;

        case BOOKMARKS:
          closeMenu(Statistics.EventName.TOOLBAR_BOOKMARKS, AlohaHelper.TOOLBAR_BOOKMARKS, new Runnable()
          {
            @Override
            public void run()
            {
              showBookmarks();
            }
          });
          break;

        case SHARE:
          closeMenu(Statistics.EventName.MENU_SHARE, AlohaHelper.MENU_SHARE, new Runnable()
          {
            @Override
            public void run()
            {
              shareMyLocation();
            }
          });
          break;

        case DOWNLOADER:
          RoutingController.get().cancel();
          closeMenu(Statistics.EventName.MENU_DOWNLOADER, AlohaHelper.MENU_DOWNLOADER, new Runnable()
          {
            @Override
            public void run()
            {
              showDownloader(false);
            }
          });
          break;

        case SETTINGS:
          closeMenu(Statistics.EventName.MENU_SETTINGS, AlohaHelper.MENU_SETTINGS, new Runnable()
          {
            @Override
            public void run()
            {
              startActivity(new Intent(MwmActivity.this, SettingsActivity.class));
            }
          });
          break;
        }
      }
    });

    if (mIsFragmentContainer)
    {
      mPanelAnimator = new PanelAnimator(this);
      mPanelAnimator.registerListener(mMainMenu.getLeftAnimationTrackListener());
      return;
    }

    if (mPlacePage.isDocked())
      mPlacePage.setLeftAnimationTrackListener(mMainMenu.getLeftAnimationTrackListener());
  }

  private void initOnmapDownloader()
  {
    mOnmapDownloader = new OnmapDownloader(this);
    if (mIsFragmentContainer)
      mPanelAnimator.registerListener(mOnmapDownloader);
  }

  @Override
  public void onDestroy()
  {
    System.out.println("STOKE: onDestroy");

    // TODO move listeners attach-deattach to onStart-onStop since onDestroy isn't guaranteed.
    Framework.nativeRemoveMapObjectListener();
    BottomSheetHelper.free();
    SearchEngine.INSTANCE.removeListener(this);
    super.onDestroy();

  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    if (!mPlacePage.isHidden())
    {
      outState.putInt(STATE_PP, mPlacePage.getState().ordinal());
      outState.putParcelable(STATE_MAP_OBJECT, mPlacePage.getMapObject());
    }

    if (!mIsFragmentContainer && RoutingController.get().isPlanning())
      mRoutingPlanInplaceController.onSaveState(outState);

    if (mIsFragmentContainer)
    {
      RoutingPlanFragment fragment = (RoutingPlanFragment) getFragment(RoutingPlanFragment.class);
      if (fragment != null)
        fragment.saveRoutingPanelState(outState);
    }

    if (mNavigationController != null)
      mNavigationController.onSaveState(outState);

    RoutingController.get().onSaveState();
    outState.putBoolean(EXTRA_LOCATION_DIALOG_IS_ANNOYING, mLocationErrorDialogAnnoying);

    mNavMyPosition.onSaveState(outState);
    if(mNavAnimationController != null)
      mNavAnimationController.onSaveState(outState);

    if (mFilterController != null)
      mFilterController.onSaveState(outState);

    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
  {
    super.onRestoreInstanceState(savedInstanceState);

    final State state = State.values()[savedInstanceState.getInt(STATE_PP, 0)];
    if (state != State.HIDDEN)
    {
      mPlacePageRestored = true;
      mPlacePage.setMapObject((MapObject) savedInstanceState.getParcelable(STATE_MAP_OBJECT), true,
                              new PlacePageView.SetMapObjectListener()
      {
        @Override
        public void onSetMapObjectComplete()
        {
          mPlacePage.setState(state);
        }
      });
    }

    if (!mIsFragmentContainer && RoutingController.get().isPlanning())
      mRoutingPlanInplaceController.restoreState(savedInstanceState);

    if (mNavigationController != null)
      mNavigationController.onRestoreState(savedInstanceState);

    mNavMyPosition.onRestoreState(savedInstanceState);
    if(mNavAnimationController != null)
      mNavAnimationController.onRestoreState(savedInstanceState);

    if (mFilterController != null)
      mFilterController.onRestoreState(savedInstanceState);
  }

  @Override
  protected void onNewIntent(Intent intent)
  {
    super.onNewIntent(intent);
    processIntent(intent);
  }

  private void processIntent(Intent intent)
  {
    if (intent == null)
      return;

    if (intent.hasExtra(EXTRA_TASK))
      addTask(intent);
    else if (intent.hasExtra(EXTRA_UPDATE_COUNTRIES))
      showDownloader(true);

    HotelsFilter filter = intent.getParcelableExtra(SearchActivity.EXTRA_HOTELS_FILTER);
    if (mFilterController != null)
    {
      mFilterController.show(filter != null || !TextUtils.isEmpty(SearchEngine.getQuery()), true);
      mFilterController.setFilter(filter);
    }
  }

  private void addTask(Intent intent)
  {
    if (intent != null &&
        !intent.getBooleanExtra(EXTRA_CONSUMED, false) &&
        intent.hasExtra(EXTRA_TASK) &&
        ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0))
    {
      final MapTask mapTask = (MapTask) intent.getSerializableExtra(EXTRA_TASK);
      mTasks.add(mapTask);
      intent.removeExtra(EXTRA_TASK);

      if (MapFragment.nativeIsEngineCreated() && mMapFragment.isContextCreated())
        runTasks();

      // mark intent as consumed
      intent.putExtra(EXTRA_CONSUMED, true);
    }
  }

  private void addTask(MapTask task)
  {
    mTasks.add(task);
    if (MapFragment.nativeIsEngineCreated() && mMapFragment.isContextCreated())
      runTasks();
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    mPlacePageRestored = mPlacePage.getState() != State.HIDDEN;
    mSearchController.refreshToolbar();
    mMainMenu.onResume(new Runnable()
    {
      @Override
      public void run()
      {
        if (Framework.nativeIsInChoosePositionMode())
        {
          UiUtils.show(mPositionChooser);
          setFullscreen(true);
        }
      }
    });
    mOnmapDownloader.onResume();
    if (mNavigationController != null)
      mNavigationController.onResume();
    if (mNavAnimationController != null)
      mNavAnimationController.onResume();
  }

  @Override
  public void recreate()
  {
    // Explicitly destroy context before activity recreation.
    mMapFragment.destroyContext();
    super.recreate();
  }

  @Override
  protected void onResumeFragments()
  {
    super.onResumeFragments();

    if (!RoutingController.get().isNavigating())
    {
      mFirstStart = FirstStartFragment.showOn(this);
      if (mFirstStart)
        return;

      if (!NewsFragment.showOn(this))
      {
        if (ViralFragment.shouldDisplay())
          new ViralFragment().show(getSupportFragmentManager(), "");
        else
          LikesManager.INSTANCE.showDialogs(this);
      }
    }

    RoutingController.get().restore();
    mPlacePage.restore();
  }



  @Override
  protected void onPause()
  {
    TtsPlayer.INSTANCE.stop();
    LikesManager.INSTANCE.cancelDialogs();
    mOnmapDownloader.onPause();
    super.onPause();
  }

  @Override
  protected void onStart()
  {
    super.onStart();
    RoutingController.get().attach(this);

    if (MapFragment.nativeIsEngineCreated())
      LocationHelper.INSTANCE.attach(this, mTelephonyManager);
    if (mTrafficButtonController != null)
      TrafficManager.INSTANCE.attach(mTrafficButtonController);
    if (mNavigationController != null)
      TrafficManager.INSTANCE.attach(mNavigationController);
  }

  @Override
  protected void onStop()
  {
    super.onStop();
    LocationHelper.INSTANCE.detach(!isFinishing());
    RoutingController.get().detach();
    TrafficManager.INSTANCE.detachAll();
    if (mTrafficButtonController != null)
      mTrafficButtonController.destroy();

    //MwmActivity.machine.done();


    System.out.println("STOKE: onStop");
  }

  @Override
  public void onBackPressed()
  {
    if (mFilterController != null && mFilterController.onBackPressed())
      return;

    if (getCurrentMenu().close(true))
    {
      mFadeView.fadeOut();
      return;
    }

    if (mSearchController.hide())
    {
      SearchEngine.cancelSearch();
      return;
    }

    if (!closePlacePage() && !closeSidePanel() && !RoutingController.get().cancel()
        && !closePositionChooser())
    {
      try
      {
        super.onBackPressed();
      } catch (IllegalStateException e)
      {
        // Sometimes this can be called after onSaveState() for unknown reason.
      }
    }
  }

  private boolean interceptBackPress()
  {
    final FragmentManager manager = getSupportFragmentManager();
    for (String tag : DOCKED_FRAGMENTS)
    {
      final Fragment fragment = manager.findFragmentByTag(tag);
      if (fragment != null && fragment.isResumed() && fragment instanceof OnBackPressListener)
        return ((OnBackPressListener) fragment).onBackPressed();
    }

    return false;
  }

  private void removeFragmentImmediate(Fragment fragment)
  {
    FragmentManager fm = getSupportFragmentManager();
    if (fm.isDestroyed())
      return;

    fm.beginTransaction()
      .remove(fragment)
      .commitAllowingStateLoss();
    fm.executePendingTransactions();
  }

  private boolean removeCurrentFragment(boolean animate)
  {
    for (String tag : DOCKED_FRAGMENTS)
      if (removeFragment(tag, animate))
        return true;

    return false;
  }

  private boolean removeFragment(String className, boolean animate)
  {
    if (animate && mPanelAnimator == null)
      animate = false;

    final Fragment fragment = getSupportFragmentManager().findFragmentByTag(className);
    if (fragment == null)
      return false;

    if (animate)
      mPanelAnimator.hide(new Runnable()
      {
        @Override
        public void run()
        {
          removeFragmentImmediate(fragment);
        }
      });
    else
      removeFragmentImmediate(fragment);

    return true;
  }

  @Override
  public void onMapObjectActivated(MapObject object)
  {
    if (MapObject.isOfType(MapObject.API_POINT, object))
    {
      final ParsedMwmRequest request = ParsedMwmRequest.getCurrentRequest();
      if (request == null)
        return;

      request.setPointData(object.getLat(), object.getLon(), object.getTitle(), object.getApiId());
      object.setSubtitle(request.getCallerName(MwmApplication.get()).toString());
    }

    setFullscreen(false);

    mPlacePage.setMapObject(object, true, new PlacePageView.SetMapObjectListener()
    {
      @Override
      public void onSetMapObjectComplete()
      {
        if (!mPlacePageRestored)
          mPlacePage.setState(State.PREVIEW);
        mPlacePageRestored = false;
      }
    });

    if (UiUtils.isVisible(mFadeView))
      mFadeView.fadeOut();
  }

  @Override
  public void onDismiss(boolean switchFullScreenMode)
  {
    if (switchFullScreenMode)
    {
      if ((mPanelAnimator != null && mPanelAnimator.isVisible()) ||
           UiUtils.isVisible(mSearchController.getToolbar()))
        return;

      setFullscreen(!mIsFullscreen);
    }
    else
    {
      mPlacePage.hide();
    }
  }

  private BaseMenu getCurrentMenu()
  {
    return (RoutingController.get().isNavigating() && mNavigationController != null
            ? mNavigationController.getNavMenu() : mMainMenu);
  }

  private void setFullscreen(boolean isFullscreen)
  {
    if (RoutingController.get().isNavigating()
            || RoutingController.get().isBuilding()
            || RoutingController.get().isPlanning())
      return;

    mIsFullscreen = isFullscreen;
    final BaseMenu menu = getCurrentMenu();

    if (isFullscreen)
    {
      if (menu.isAnimating())
        return;

      mIsFullscreenAnimating = true;
      Animations.disappearSliding(menu.getFrame(), Animations.BOTTOM, new Runnable()
      {
        @Override
        public void run()
        {
          final int menuHeight = menu.getFrame().getHeight();
          adjustRuler(0, menuHeight);

          mIsFullscreenAnimating = false;
          if (mIsAppearMenuLater)
          {
            appearMenu(menu);
            mIsAppearMenuLater = false;
          }
        }
      });
      if (mNavAnimationController != null)
        mNavAnimationController.disappearZoomButtons();
      mNavMyPosition.hide();
      mTraffic.hide();
    }
    else
    {
      if (mPlacePage.isHidden() && mNavAnimationController != null)
        mNavAnimationController.appearZoomButtons();
      if (!mIsFullscreenAnimating)
        appearMenu(menu);
      else
        mIsAppearMenuLater = true;
    }
  }

  private void appearMenu(BaseMenu menu)
  {
    Animations.appearSliding(menu.getFrame(), Animations.BOTTOM, new Runnable()
    {
      @Override
      public void run()
      {
        adjustRuler(0, 0);
      }
    });
    mNavMyPosition.show();
    mTraffic.show();
  }

  @Override
  public void onPreviewVisibilityChanged(boolean isVisible)
  {
    if (mVisibleRectMeasurer != null)
      mVisibleRectMeasurer.setPreviewVisible(isVisible);

    if (isVisible)
    {
      if (mMainMenu.isAnimating() || mMainMenu.isOpen())
        UiThread.runLater(new Runnable()
        {
          @Override
          public void run()
          {
            if (mMainMenu.close(true))
              mFadeView.fadeOut();
          }
        }, BaseMenu.ANIMATION_DURATION * 2);
    }
    else
    {
      Framework.nativeDeactivatePopup();
      mPlacePage.setMapObject(null, false, null);
    }
  }

  @Override
  public void onPlacePageVisibilityChanged(boolean isVisible)
  {
    if (mVisibleRectMeasurer != null)
      mVisibleRectMeasurer.setPlacePageVisible(isVisible);

    Statistics.INSTANCE.trackEvent(isVisible ? Statistics.EventName.PP_OPEN
                                             : Statistics.EventName.PP_CLOSE);
    AlohaHelper.logClick(isVisible ? AlohaHelper.PP_OPEN
                                   : AlohaHelper.PP_CLOSE);
  }

  @Override
  public void onProgress(float translationX, float translationY)
  {
    if (mNavAnimationController != null)
      mNavAnimationController.onPlacePageMoved(translationY);
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
    case R.id.nav_zoom_in:
      Statistics.INSTANCE.trackEvent(Statistics.EventName.ZOOM_IN);
      AlohaHelper.logClick(AlohaHelper.ZOOM_IN);
      MapFragment.nativeScalePlus();
      break;
    case R.id.nav_zoom_out:
      Statistics.INSTANCE.trackEvent(Statistics.EventName.ZOOM_OUT);
      AlohaHelper.logClick(AlohaHelper.ZOOM_OUT);
      MapFragment.nativeScaleMinus();
      break;
      /*
    case R.id.feedback_plus:
      machine.userFeedback(Feedback.YES);
      break;
    case R.id.feedback_minus:
      machine.userFeedback(Feedback.NO);
      break;
      */
    }
  }

  @Override
  public boolean onTouch(View view, MotionEvent event)
  {
    return mPlacePage.hideOnTouch() ||
           mMapFragment.onTouch(view, event);
  }

  @Override
  public void customOnNavigateUp()
  {
    if (removeCurrentFragment(true))
    {
      InputUtils.hideKeyboard(mMainMenu.getFrame());
      mSearchController.refreshToolbar();
    }
  }

  public interface MapTask extends Serializable
  {
    boolean run(MwmActivity target);
  }

  public static class OpenUrlTask implements MapTask
  {
    private static final long serialVersionUID = 1L;
    private final String mUrl;

    OpenUrlTask(String url)
    {
      Utils.checkNotNull(url);
      mUrl = url;
    }

    @Override
    public boolean run(MwmActivity target)
    {
      final @ParsedUrlMwmRequest.ParsingResult int result = Framework.nativeParseAndSetApiUrl(mUrl);
      switch (result)
      {
      case ParsedUrlMwmRequest.RESULT_INCORRECT:
        // TODO: Kernel recognizes "mapsme://", "mwm://" and "mapswithme://" schemas only!!!
        return MapFragment.nativeShowMapForUrl(mUrl);

      case ParsedUrlMwmRequest.RESULT_MAP:
        return MapFragment.nativeShowMapForUrl(mUrl);

      case ParsedUrlMwmRequest.RESULT_ROUTE:
        final ParsedRoutingData data = Framework.nativeGetParsedRoutingData();
        RoutingController.get().setRouterType(data.mRouterType);
        final RoutePoint from = data.mPoints[0];
        final RoutePoint to = data.mPoints[1];
        RoutingController.get().prepare(new MapObject(MapObject.API_POINT, from.mName, "", "",
                                                      from.mLat, from.mLon, "", Banner.EMPTY, false),
                                        new MapObject(MapObject.API_POINT, to.mName, "", "",
                                                      to.mLat, to.mLon, "", Banner.EMPTY, false));
        return true;
      }

      return false;
    }
  }

  public static class ShowCountryTask implements MapTask
  {
    private static final long serialVersionUID = 1L;
    private final String mCountryId;
    private final boolean mDoAutoDownload;

    public ShowCountryTask(String countryId, boolean doAutoDownload)
    {
      mCountryId = countryId;
      mDoAutoDownload = doAutoDownload;
    }

    @Override
    public boolean run(MwmActivity target)
    {
      if (mDoAutoDownload)
        MapManager.warn3gAndDownload(target, mCountryId, null);

      Framework.nativeShowCountry(mCountryId, mDoAutoDownload);
      return true;
    }
  }

  void adjustCompass(int offsetY)
  {
    if (mMapFragment == null || !mMapFragment.isAdded())
      return;

    mMapFragment.setupCompass(offsetY, true);

    CompassData compass = LocationHelper.INSTANCE.getCompassData();
    if (compass != null)
      MapFragment.nativeCompassUpdated(compass.getMagneticNorth(), compass.getTrueNorth(), true);
  }

  private void adjustRuler(int offsetX, int offsetY)
  {
    if (mMapFragment == null || !mMapFragment.isAdded())
      return;

    mMapFragment.setupRuler(offsetX, offsetY, true);
  }

  @Override
  public FragmentActivity getActivity()
  {
    return this;
  }

  public MainMenu getMainMenu()
  {
    return mMainMenu;
  }

  @Override
  public void showSearch()
  {
    showSearch("");
  }

  @Override
  public void updateMenu()
  {
    adjustMenuLineFrameVisibility(new Runnable()
    {
      @Override
      public void run()
      {
        if (RoutingController.get().isNavigating())
        {
          if (mNavigationController != null)
            mNavigationController.show(true);
          mSearchController.hide();
          mMainMenu.setState(MainMenu.State.NAVIGATION, false, mIsFullscreen);
          return;
        }

        if (mIsFragmentContainer)
        {
          mMainMenu.setEnabled(MainMenu.Item.P2P, !RoutingController.get().isPlanning());
          mMainMenu.setEnabled(MainMenu.Item.SEARCH, !RoutingController.get().isWaitingPoiPick());
        }
        else if (RoutingController.get().isPlanning())
        {
          mMainMenu.setState(MainMenu.State.ROUTE_PREPARE, false, mIsFullscreen);
          return;
        }

        mMainMenu.setState(MainMenu.State.MENU, false, mIsFullscreen);
      }
    });
  }

  private void adjustMenuLineFrameVisibility(@Nullable final Runnable completion)
  {
    final RoutingController controller = RoutingController.get();

    if (controller.isBuilt() || controller.isUberRequestHandled())
    {
      mMainMenu.showLineFrame(true, new Runnable()
      {
        @Override
        public void run()
        {
          adjustRuler(0, 0);
        }
      });

      if (completion != null)
        completion.run();
      return;
    }

    if (controller.isPlanning() || controller.isBuilding() || controller.isErrorEncountered())
    {
      mMainMenu.showLineFrame(false, new Runnable()
      {
        @Override
        public void run()
        {
          final int menuHeight = getCurrentMenu().getFrame().getHeight();
          adjustRuler(0, menuHeight);
          if (completion != null)
            completion.run();
        }
      });
      return;
    }

    mMainMenu.showLineFrame(true, new Runnable()
    {
      @Override
      public void run()
      {
        adjustRuler(0, 0);
      }
    });

    if (completion != null)
      completion.run();
  }

  private void setNavButtonsTopLimit(int limit)
  {
    if (mNavAnimationController == null)
      return;

    mNavAnimationController.setTopLimit(limit);
  }

  @Override
  public void showRoutePlan(boolean show, @Nullable Runnable completionListener)
  {
    if (mNavAnimationController != null && !mIsFragmentContainer)
    {
      mNavAnimationController.setBottomLimit(show ? 0 : getCurrentMenu().getFrame().getHeight());
      mNavAnimationController.slide(show, getCurrentMenu().getFrame().getHeight());
    }
    if (show)
    {
      mSearchController.hide();

      if (mIsFragmentContainer)
      {
        replaceFragment(RoutingPlanFragment.class, null, completionListener);
        adjustTraffic(UiUtils.dimen(R.dimen.panel_width), UiUtils.getStatusBarHeight(getApplicationContext()));
      }
      else
      {
        mRoutingPlanInplaceController.show(true);
        if (completionListener != null)
          completionListener.run();

        int routingPlanPanelHeight = mRoutingPlanInplaceController.getHeight();
        if (routingPlanPanelHeight > 0)
        {
          adjustCompassAndTraffic(routingPlanPanelHeight);
          setNavButtonsTopLimit(routingPlanPanelHeight);
        }
      }
    }
    else
    {
      if (mIsFragmentContainer)
        adjustTraffic(0, UiUtils.getStatusBarHeight(getApplicationContext()));
      else
        mRoutingPlanInplaceController.show(false);

      closeAllFloatingPanels();

      if (completionListener != null)
        completionListener.run();

      adjustCompassAndTraffic(UiUtils.getStatusBarHeight(getApplicationContext()));
      setNavButtonsTopLimit(0);

      updateSearchBar();
    }

    mPlacePage.refreshViews();
  }

  private void adjustCompassAndTraffic(int offsetY)
  {
    adjustCompass(offsetY);
    adjustTraffic(0, offsetY);
  }

  private void adjustTraffic(int offsetX, int offsetY)
  {
    mTraffic.setOffset(offsetX, offsetY);
  }

  @Override
  public void onToggle(boolean state)
  {
    if (mNavAnimationController == null)
      return;

    int routingPlanPanelHeight = mRoutingPlanInplaceController.getHeight();
    adjustCompassAndTraffic(routingPlanPanelHeight);
    setNavButtonsTopLimit(routingPlanPanelHeight + UiUtils.dimen(R.dimen.compass_height));
  }

  @Override
  public void onSearchVisibilityChanged(boolean visible)
  {
    if (mNavAnimationController == null)
      return;

    int toolbarHeight = mSearchController.getToolbar().getHeight();
    adjustCompassAndTraffic(visible ? toolbarHeight : UiUtils.getStatusBarHeight(this));
    setNavButtonsTopLimit(visible ? toolbarHeight : 0);
    if (mFilterController != null)
    {
      boolean show = visible && !TextUtils.isEmpty(SearchEngine.getQuery());
      mFilterController.show(show, true);
      mMainMenu.show(!show);
    }
  }

  @Override
  public void onResultsUpdate(SearchResult[] results, long timestamp, boolean isHotel)
  {
    if (mFilterController != null)
      mFilterController.updateFilterButtonVisibility(isHotel);
  }

  @Override
  public void onResultsEnd(long timestamp)
  {
  }

  @Override
  public void showNavigation(boolean show)
  {
    mPlacePage.refreshViews();
    if (mNavigationController != null)
      mNavigationController.show(show);
    refreshFade();
    mOnmapDownloader.updateState(false);
    adjustCompass(UiUtils.getCompassYOffset(this));
  }

  @Override
  public void updatePoints()
  {
    if (mIsFragmentContainer)
    {
      RoutingPlanFragment fragment = (RoutingPlanFragment) getFragment(RoutingPlanFragment.class);
      if (fragment != null)
        fragment.updatePoints();
    }
    else
    {
      mRoutingPlanInplaceController.updatePoints();
    }
  }

  @Override
  public void updateBuildProgress(int progress, @Framework.RouterType int router)
  {
    if (mIsFragmentContainer)
    {
      RoutingPlanFragment fragment = (RoutingPlanFragment) getFragment(RoutingPlanFragment.class);
      if (fragment != null)
        fragment.updateBuildProgress(progress, router);
    }
    else
    {
      mRoutingPlanInplaceController.updateBuildProgress(progress, router);
    }
  }

  @Override
  public void animateSearchPoiTransition(@NonNull final Rect startRect,
                                         @Nullable final Runnable runnable)
  {
    Animations.riseTransition(mRootView, startRect, runnable);
  }

  @Override
  public void onUberInfoReceived(@NonNull UberInfo info)
  {
    if (mIsFragmentContainer)
    {
      RoutingPlanFragment fragment = (RoutingPlanFragment) getFragment(RoutingPlanFragment.class);
      if (fragment != null)
        fragment.showUberInfo(info);
    }
    else
    {
      mRoutingPlanInplaceController.showUberInfo(info);
    }
  }

  @Override
  public void onUberError(@NonNull Uber.ErrorCode code)
  {
    if (mIsFragmentContainer)
    {
      RoutingPlanFragment fragment = (RoutingPlanFragment) getFragment(RoutingPlanFragment.class);
      if (fragment != null)
        fragment.showUberError(code);
    }
    else
    {
      mRoutingPlanInplaceController.showUberError(code);
    }
  }

  @Override
  public void onNavigationCancelled()
  {
    if (mNavigationController != null)
      mNavigationController.stop(this);
    updateSearchBar();
  }

  private void updateSearchBar()
  {
    if (!TextUtils.isEmpty(SearchEngine.getQuery()))
      mSearchController.refreshToolbar();
  }

  boolean isFirstStart()
  {
    boolean res = mFirstStart;
    mFirstStart = false;
    return res;
  }

  @Override
  public void onMyPositionModeChanged(int newMode)
  {
    mNavMyPosition.update(newMode);
  }

  @Override
  public void onLocationUpdated(@NonNull Location location)
  {
    if (!mPlacePage.isHidden())
      mPlacePage.refreshLocation(location);

    if (!RoutingController.get().isNavigating())
      return;

    if (mNavigationController != null)
      mNavigationController.update(Framework.nativeGetRouteFollowingInfo());

    TtsPlayer.INSTANCE.playTurnNotifications();
  }

  @Override
  public void onCompassUpdated(@NonNull CompassData compass)
  {
    if (LocationHelper.INSTANCE.getSensorRate() == MwmActivity.SENSOR_FULL) {
      MapFragment.nativeCompassUpdated(compass.getMagneticNorth(), compass.getTrueNorth(), false);
      mPlacePage.refreshAzimuth(compass.getNorth());
      if (mNavigationController != null)
        mNavigationController.updateNorth(compass.getNorth());
    }
  }

  @Override
  public void onLocationError()
  {
    if (mLocationErrorDialogAnnoying)
      return;

    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    if (intent.resolveActivity(MwmApplication.get().getPackageManager()) == null)
    {
      intent = new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS);
      if (intent.resolveActivity(MwmApplication.get().getPackageManager()) == null)
        return;
    }

    showLocationErrorDialog(intent);
  }

  private void showLocationErrorDialog(@NonNull final Intent intent)
  {
    if (mLocationErrorDialog != null && mLocationErrorDialog.isShowing())
      return;

    mLocationErrorDialog = new AlertDialog.Builder(this)
        .setTitle(R.string.enable_location_services)
        .setMessage(R.string.location_is_disabled_long_text)
        .setNegativeButton(R.string.close, new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            mLocationErrorDialogAnnoying = true;
          }
        })
        .setOnCancelListener(new DialogInterface.OnCancelListener()
        {
          @Override
          public void onCancel(DialogInterface dialog)
          {
            mLocationErrorDialogAnnoying = true;
          }
        })
        .setPositiveButton(R.string.connection_settings, new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            startActivity(intent);
          }
        }).show();
  }

  @Override
  public void onLocationNotFound()
  {
    showLocationNotFoundDialog();
  }

  private void showLocationNotFoundDialog()
  {
    String message = String.format("%s\n\n%s", getString(R.string.current_location_unknown_message),
                                   getString(R.string.current_location_unknown_title));
    new AlertDialog.Builder(this)
        .setMessage(message)
        .setNegativeButton(R.string.current_location_unknown_stop_button, null)
        .setPositiveButton(R.string.current_location_unknown_continue_button, new DialogInterface.OnClickListener()
        {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            LocationHelper.INSTANCE.start();
          }
        }).show();
  }

  public static class ShowAuthorizationTask implements MapTask
  {
    @Override
    public boolean run(MwmActivity target)
    {
      final DialogFragment fragment = (DialogFragment) Fragment.instantiate(target, AuthDialogFragment.class.getName());
      fragment.show(target.getSupportFragmentManager(), AuthDialogFragment.class.getName());
      return true;
    }
  }

  public static abstract class BaseUserMarkTask implements MapTask
  {
    private static final long serialVersionUID = 1L;

    final int mCategoryId;
    final int mId;

    BaseUserMarkTask(int categoryId, int id)
    {
      mCategoryId = categoryId;
      mId = id;
    }
  }

  public static class ShowBookmarkTask extends BaseUserMarkTask
  {
    public ShowBookmarkTask(int categoryId, int bookmarkId)
    {
      super(categoryId, bookmarkId);
    }

    @Override
    public boolean run(MwmActivity target)
    {
      BookmarkManager.INSTANCE.nativeShowBookmarkOnMap(mCategoryId, mId);
      return true;
    }
  }

  public static class ShowTrackTask extends BaseUserMarkTask
  {
    public ShowTrackTask(int categoryId, int trackId)
    {
      super(categoryId, trackId);
    }

    @Override
    public boolean run(MwmActivity target)
    {
      Framework.nativeShowTrackRect(mCategoryId, mId);
      return true;
    }
  }
}
