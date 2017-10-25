package com.scholefield.lee.androidtemplate;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.*;
import android.support.test.espresso.util.HumanReadables;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.core.AllOf.allOf;

/**
 * A collection of {@link android.support.test.espresso.ViewAction}s for {@code RecyclerView}s
 */
public class RecyclerViewActions {

    /**
     * Performs the {@code viewAction} on a {@code ViewHolder} at the given position.
     *
     * @param position position of the viewHolder to perform the action on.
     * @param viewAction {@code ViewAction} to perform.
     * @return Action to perform.
     */
    public static <VH extends RecyclerView.ViewHolder> ViewAction actionOnItemAtPosition(final int position,
                                                                                         final ViewAction viewAction) {
        return new ActionOnItemAtPositionViewAction<VH>(position, viewAction);
    }

    /**
     * Scrolls to the {@code position} at the RecyclerView.
     */
    public static ViewAction scrollToPosition(final int position) {
        return new ScrollToPositionViewAction(position);
    }

    public static ViewAction dragItemAtPosition(final int position, final int xPosEnd, final int yPosEnd) {

        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isDisplayed();
            }

            @Override
            public String getDescription() {
                return "Send touch events.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView rv = (RecyclerView) view;

                RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    throw new PerformException.Builder().withActionDescription(this.toString())
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(new IllegalStateException("No view holder at position: " + position))
                            .build();
                }

                GeneralSwipeAction action = new GeneralSwipeAction(Swipe.FAST, GeneralLocation.TOP_CENTER, GeneralLocation.BOTTOM_CENTER, Press.FINGER);
                action.perform(uiController, view);
            }

            private void drag(final UiController controller, final float[] startPos, final float[] endPos) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        controller.loopMainThreadForAtLeast(10000);
                        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

                        long downTime = SystemClock.uptimeMillis();
                        long eventTime = SystemClock.uptimeMillis();

                        float x = startPos[0];
                        float y = startPos[1];

                        float xStep = (endPos[0] - startPos[0]) / 30;
                        float yStep = (endPos[1] - startPos[1]) / 30;

                        MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);
                        instrumentation.sendPointerSync(event);

                        for (int i = 0; i < 30; i++) {
                            x += xStep;
                            y += yStep;
                            eventTime = SystemClock.uptimeMillis();
                            event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y, 0);
                            instrumentation.sendPointerSync(event);
                        }

                        eventTime = SystemClock.uptimeMillis();
                        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
                        instrumentation.sendPointerSync(event);
                        instrumentation.waitForIdleSync();
                    }

                };
                new Thread(run).start();
            }

            private void dragInternal(final UiController uiController, float[] startPos, float[] endPos) throws Exception{
                long downTime = SystemClock.uptimeMillis();
                long eventTime = SystemClock.uptimeMillis();

                float x = startPos[0];
                float y = startPos[1];

                float xStep = (endPos[0] - startPos[0]) / 30;
                float yStep = (endPos[1] - startPos[1]) / 30;

                MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);
                uiController.injectMotionEvent(event);

                for (int i = 0; i < 30; i++) {
                    x += xStep;
                    y += yStep;
                    eventTime = SystemClock.uptimeMillis();
                    event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y, 0);
                    uiController.injectMotionEvent(event);
                }

                eventTime = SystemClock.uptimeMillis();
                event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
                uiController.injectMotionEvent(event);
            }

        };

    }


    /**
     * ViewAction implementation. Performs the given action on the ViewHolder at a given position.
     */
    private static final class ActionOnItemAtPositionViewAction<VH extends RecyclerView.ViewHolder> implements
            ViewAction {

        private final int position;
        private final ViewAction viewAction;

        /**
         * @param position position of the ViewHolder to perform the action on.
         * @param viewAction {@code ViewAction} to perform.
         */
        private ActionOnItemAtPositionViewAction(int position, ViewAction viewAction) {
            this.position = position;
            this.viewAction = viewAction;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Matcher<View> getConstraints() {
            return allOf(isAssignableFrom(RecyclerView.class), isDisplayed());
        }

        @Override
        public String getDescription() {
            return "actionOnItemAtPosition performing ViewAction: " + viewAction.getDescription()
                    + " on item at position: " + position;
        }

        @Override
        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;

            // scroll to position and then wait on ui thread.
            new ScrollToPositionViewAction(position).perform(uiController, view);
            uiController.loopMainThreadUntilIdle();

            @SuppressWarnings("unchecked")
            VH viewHolderForPosition = (VH) recyclerView.findViewHolderForLayoutPosition(position);
            if (null == viewHolderForPosition) {
                throw new PerformException.Builder().withActionDescription(this.toString())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new IllegalStateException("No view holder at position: " + position))
                        .build();
            }

            View viewAtPosition = viewHolderForPosition.itemView;
            if (null == viewAtPosition) {
                throw new PerformException.Builder().withActionDescription(this.toString())
                        .withViewDescription(HumanReadables.describe(viewAtPosition))
                        .withCause(new IllegalStateException("No view at position: " + position)).build();
            }

            viewAction.perform(uiController, viewAtPosition);
        }
    }

    private static final class ScrollToPositionViewAction implements ViewAction {
        private final int position;

        private ScrollToPositionViewAction(int position) {
            this.position = position;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Matcher<View> getConstraints() {
            return allOf(isAssignableFrom(RecyclerView.class), isDisplayed());
        }

        @Override
        public String getDescription() {
            return "scroll RecyclerView to position: " + position;
        }

        @Override
        public void perform(UiController uiController, View view) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.scrollToPosition(position);
        }
    }
}
