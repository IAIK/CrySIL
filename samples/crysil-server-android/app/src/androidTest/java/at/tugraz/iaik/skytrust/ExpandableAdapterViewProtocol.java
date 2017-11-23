package at.tugraz.iaik.skytrust;

import android.database.Cursor;
import android.os.Build;
import android.support.test.espresso.action.AdapterViewProtocol;
import android.support.test.espresso.action.AdapterViewProtocols;
import android.support.test.espresso.core.deps.guava.base.Optional;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.support.test.espresso.core.deps.guava.collect.Range;
import android.support.test.espresso.matcher.ViewMatchers;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.AdapterViewFlipper;
import android.widget.ExpandableListView;

import com.google.common.collect.Lists;

import java.util.List;

import at.tugraz.iaik.skytrust.utils.AccountCursorTreeAdapter;

/**
 * With help from https://gist.github.com/brennantaylor/9379399
 */
public class ExpandableAdapterViewProtocol implements AdapterViewProtocol {

    private static final class StandardDataFunction implements DataFunction {
        private final Object dataAtPosition;
        private final int position;

        private StandardDataFunction(Object dataAtPosition, int position) {
            Preconditions.checkArgument(position >= 0, "position must be >= 0");
            this.dataAtPosition = dataAtPosition;
            this.position = position;
        }

        public Object getData() {
            if (this.dataAtPosition instanceof Cursor && !((Cursor) this.dataAtPosition).moveToPosition(
                    this.position)) {
                Log.e("ExpandableAVP", "Cannot move cursor to position: " + this.position);
            }
            return this.dataAtPosition;
        }
    }

    @Override
    public Iterable<AdaptedData> getDataInAdapterView(AdapterView<? extends Adapter> adapterView) {
        if (adapterView instanceof ExpandableListView) {
            ExpandableListView expandableListView = (ExpandableListView) adapterView;
            AccountCursorTreeAdapter multiAdapter = (AccountCursorTreeAdapter) expandableListView.getExpandableListAdapter();
            List<AdaptedData> datas = Lists.newArrayList();
            for (int group = 0; group < multiAdapter.getGroupCount(); ++group) {
                for (int child = 0; child < multiAdapter.getChildrenCount(group); ++child) {
                    Cursor cursor = multiAdapter.getChild(group, child);
                    long token = ExpandableListView.getPackedPositionForChild(group, child);
                    datas.add(new AdaptedData.Builder().withDataFunction(
                            new StandardDataFunction(cursor, cursor.getPosition())).withOpaqueToken(
                            Long.valueOf(token)).build());
                }
            }
            return datas;
        }
        return AdapterViewProtocols.standardProtocol().getDataInAdapterView(adapterView);
    }

    @Override
    public Optional<AdaptedData> getDataRenderedByView(AdapterView<? extends Adapter> adapterView,
                                                       View descendantView) {
        if (adapterView == descendantView.getParent()) {
            int position = adapterView.getPositionForView(descendantView);
            if (position != AdapterView.INVALID_POSITION) {
                ExpandableListView expandableListView = (ExpandableListView) adapterView;
                AccountCursorTreeAdapter multiAdapter = (AccountCursorTreeAdapter) expandableListView.getExpandableListAdapter();
                long packedPosition = expandableListView.getExpandableListPosition(position);
                int group = ExpandableListView.getPackedPositionGroup(packedPosition);
                int child = ExpandableListView.getPackedPositionChild(packedPosition);
                Cursor cursor = multiAdapter.getChild(group, child);
                return Optional.of(new AdaptedData.Builder().withDataFunction(
                        new StandardDataFunction(cursor, position)).withOpaqueToken(
                        Long.valueOf(packedPosition)).build());
            }
        }
        return Optional.absent();
    }

    @Override
    public void makeDataRenderedWithinAdapterView(AdapterView<? extends Adapter> adapterView, AdaptedData adaptedData) {
        Preconditions.checkArgument(adaptedData.opaqueToken instanceof Long, "Not my adaptedData: %s",
                new Object[]{adaptedData});

        ExpandableListView expandableListView = (ExpandableListView) adapterView;
        long packedPosition = ((Long) adaptedData.opaqueToken).longValue();
        int dataPosition = expandableListView.getFlatListPosition(packedPosition);

        boolean moved = false;
        if (Build.VERSION.SDK_INT > 7) {
            if (adapterView instanceof AbsListView) {
                if (Build.VERSION.SDK_INT > 10) {
                    ((AbsListView) adapterView).smoothScrollToPositionFromTop(dataPosition, adapterView.getPaddingTop(),
                            0);
                } else {
                    ((AbsListView) adapterView).smoothScrollToPosition(dataPosition);
                }

                moved = true;
            }

            if (Build.VERSION.SDK_INT > 10 && adapterView instanceof AdapterViewAnimator) {
                if (adapterView instanceof AdapterViewFlipper) {
                    ((AdapterViewFlipper) adapterView).stopFlipping();
                }

                ((AdapterViewAnimator) adapterView).setDisplayedChild(dataPosition);
                moved = true;
            }
        }

        if (!moved) {
            expandableListView.getChildAt(dataPosition).setSelected(true);
            expandableListView.setSelection(dataPosition);
        }
    }

    @Override
    public boolean isDataRenderedWithinAdapterView(AdapterView<? extends Adapter> adapterView,
                                                   AdaptedData adaptedData) {
        Preconditions.checkArgument(adaptedData.opaqueToken instanceof Long, "Not my data: %s",
                new Object[]{adaptedData});
        ExpandableListView expandableListView = (ExpandableListView) adapterView;
        long packedPosition = ((Long) adaptedData.opaqueToken).longValue();
        int dataPosition = expandableListView.getFlatListPosition(packedPosition);
        boolean inView = false;
        if (Range.closed(Integer.valueOf(adapterView.getFirstVisiblePosition()),
                Integer.valueOf(adapterView.getLastVisiblePosition())).contains(Integer.valueOf(dataPosition))) {
            if (adapterView.getFirstVisiblePosition() == adapterView.getLastVisiblePosition()) {
                inView = true;
            } else {
                inView = this.isElementFullyRendered(adapterView, dataPosition - adapterView.getFirstVisiblePosition());
            }
        }

        if (inView) {
            expandableListView.getChildAt(dataPosition).setSelected(true);
            expandableListView.setSelection(dataPosition);
        }

        return inView;
    }

    private boolean isElementFullyRendered(AdapterView<? extends Adapter> adapterView, int childAt) {
        ExpandableListView expandableListView = (ExpandableListView) adapterView;
        View element = expandableListView.getChildAt(childAt);
        return ViewMatchers.isDisplayingAtLeast(90).matches(element);
    }
}
