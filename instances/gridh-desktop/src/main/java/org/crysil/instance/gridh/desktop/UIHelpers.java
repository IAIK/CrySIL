package org.crysil.instance.gridh.desktop;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.apache.pivot.collections.ArrayList;
import org.apache.pivot.collections.List;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.CalendarDate;
import org.apache.pivot.util.Filter;
import org.apache.pivot.util.ListenerList;
import org.apache.pivot.wtk.ActivityIndicator;
import org.apache.pivot.wtk.BoxPane;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.CalendarButton;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.ComponentKeyListener;
import org.apache.pivot.wtk.Keyboard;
import org.apache.pivot.wtk.Keyboard.KeyLocation;
import org.apache.pivot.wtk.Label;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.Meter;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.content.ButtonDataRenderer;
import org.crysil.gridh.io.storage.dropfile.DropFileOutputStream;
import org.crysil.gridh.io.storage.local.LocalFileOutputStream;

abstract class UIHelpers {

  private static final String OUTPUT_LOCATION_PLACEHOLDER = "…";

  static class ProgressIndicator extends BoxPane {
    private final Label             title;
    private final ActivityIndicator indicator;

    private ProgressIndicator(final int width, final int height, final String title) {
      super();
      indicator = new ActivityIndicator();
      indicator.setPreferredSize(width, height);
      indicator.setActive(true);
      this.title = new Label(title);

      try {
        setStyles("{fill:'true'}");
        this.title.setStyles("{verticalAlignment: 'center'}");
      } catch (final SerializationException e) {
        e.printStackTrace();
      }
      add(indicator);
      add(this.title);
    }

    public void setTitle(final String title) {
      this.title.setText(title);
    }
  }

  static boolean isButtonEmpty(final Button btn) {
    return btn.getButtonData() == null || btn.getButtonData().equals("");
  }

  static ButtonDataRenderer setupLocationRenderer() {
    return new ButtonDataRenderer() {
      @Override
      public void render(final Object data, final Button button, final boolean highlighted) {
        if (data == null || "".equals(data)) {
          super.render(OUTPUT_LOCATION_PLACEHOLDER, button, highlighted);
        } else {
          final String d = ((File) data).getAbsolutePath();
          if (d.contains(File.separator)) {
            final String[] parts = d.split(Pattern.quote(File.separator));
            super.render(parts[parts.length - 1], button, highlighted);
          } else {
            super.render(data, button, highlighted);
          }
        }
      }
    };
  }

  static Filter<Integer> setupMinuteFilter(final CalendarButton calendar, final ListButton lstHour) {
    return new Filter<Integer>() {
      public boolean include(final Integer item) {
        if (calendar.getSelectedDate().compareTo(new CalendarDate()) != 0) {
          return false;
        }
        if ((Integer) lstHour.getSelectedItem() != GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
          return false;
        }
        return item < GregorianCalendar.getInstance().get(Calendar.MINUTE);
      }
    };
  }

  static Filter<Integer> setupHourFilter(final CalendarButton calendar) {
    return new Filter<Integer>() {
      public boolean include(final Integer item) {
        if (calendar.getSelectedDate().compareTo(new CalendarDate()) != 0) {
          return false;
        }
        return GregorianCalendar.getInstance().get(Calendar.HOUR_OF_DAY) > item;
      }
    };
  }

  static Filter<CalendarDate> setupDateFilter() {
    final Filter<CalendarDate> filter = new Filter<CalendarDate>() {
      public boolean include(final CalendarDate item) {
        final CalendarDate now = new CalendarDate();
        return item.compareTo(now) < 0;
      }
    };
    return filter;
  }

  static Meter getKnownProgressMeter(final int height, final int width) {
    final Meter progressMeter = new Meter();
    progressMeter.setPreferredSize(width, height);
    return progressMeter;
  }

  static ProgressIndicator getUnknownProgressIndicator(final int height, final int width, final String title) {
    return new ProgressIndicator(width, height, title);
  }

  static void setupStorageChoice(final ListButton lstSotrage) {
    lstSotrage.setListData(setupStorageList());
    lstSotrage.setSelectedIndex(0);
  }

  private static List<String> setupStorageList() {
    final ArrayList<String> lstStorage = new ArrayList<String>(2);
    lstStorage.add(DropFileOutputStream.getFriendlyName());
    lstStorage.add(LocalFileOutputStream.getFriendlyName());
    return lstStorage;
  }

  static void setupSheetBehavior(final Sheet sheet, final boolean cancellable) {
    final ListenerList<ComponentKeyListener> componentKeyListeners = sheet.getComponentKeyListeners();
    while (!componentKeyListeners.isEmpty()) {
      componentKeyListeners.remove(componentKeyListeners.iterator().next());
    }
    componentKeyListeners.add(new ComponentKeyListener.Adapter() {
      @Override
      public boolean keyPressed(final Component component, final int keyCode, final KeyLocation keyLocation) {
        final Sheet sheet = (Sheet) component;
        if (cancellable) {
          if (keyCode == Keyboard.KeyCode.ESCAPE) {
            sheet.close(false);
            return true;
          }
        }

        return false;
      }
    });
  }

}
