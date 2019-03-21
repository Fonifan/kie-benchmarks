package org.jboss.qa.brms.performance.examples.nurserostering.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.jboss.qa.brms.performance.examples.common.domain.AbstractPersistable;

@XStreamAlias("BoundaryDate")
public class BoundaryDate extends AbstractPersistable {

    // magic value that is beyond reasonable dayIndex range and still allows delta of indices to be an Integer
    public static final int noNextPreferredDayIndex = Integer.MAX_VALUE / 3;
    public static final int noPrevPreferredDayIndex = Integer.MIN_VALUE / 3;
    /**
     *
     */
    private static final long serialVersionUID = -7393276689810490427L;
    private static final DateTimeFormatter LABEL_FORMATTER = DateTimeFormatter.ofPattern("E d MMM");
    private int dayIndex; // TODO check if still needed/faster now that we use LocalDate instead of java.util.Date
    private LocalDate date;
    private boolean preferredSequenceStart;    // true means "this date is a preferred start to assignment sequences"
    private boolean preferredSequenceEnd;    // true means "this date is a preferred end for assignment sequences"
    private int nextPreferredStartDayIndex;    // MAX_VALUE means "none"; if preferredSequenceStart is true, then this ref is still to the FUTURE next pref start date
    private int prevPreferredStartDayIndex;    // MIN_VALUE means "none"; if preferredSequenceStart is true, then this ref is still to the PREVIOUS next pref start date

    public static int getForwardDaysToReach(DayOfWeek startDayOfWeek, DayOfWeek targetDayOfWeek) {
        if (startDayOfWeek == targetDayOfWeek) {
            return 0;
        }
        int forwardDayCount = 1;
        while (startDayOfWeek.plus(forwardDayCount) != targetDayOfWeek) {
            forwardDayCount++;

            if (forwardDayCount > 10) {
                throw new IllegalStateException("counting forward in days from " + startDayOfWeek + " never found target day of week: " + targetDayOfWeek);
            }
        }
        return forwardDayCount;
    }

    public static int getBackwardDaysToReach(DayOfWeek startDayOfWeek, DayOfWeek targetDayOfWeek) {
        if (startDayOfWeek == targetDayOfWeek) {
            return 0;
        }
        int backwardDayCount = 1;
        while (startDayOfWeek.minus(backwardDayCount) != targetDayOfWeek) {
            backwardDayCount++;

            if (backwardDayCount > 10) {
                throw new IllegalStateException("counting backward in days from " + startDayOfWeek + " never found target day of week: " + targetDayOfWeek);
            }
        }
        return backwardDayCount;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isPreferredSequenceStart() {
        return preferredSequenceStart;
    }

    public void setPreferredSequenceStart(boolean preferredSequenceStart) {
        this.preferredSequenceStart = preferredSequenceStart;
    }

    public boolean isPreferredSequenceEnd() {
        return preferredSequenceEnd;
    }

    public void setPreferredSequenceEnd(boolean preferredSequenceEnd) {
        this.preferredSequenceEnd = preferredSequenceEnd;
    }

    public int getNextPreferredStartDayIndex() {
        return nextPreferredStartDayIndex;
    }

    public void setNextPreferredStartDayIndex(int nextPreferredStartDayIndex) {
        this.nextPreferredStartDayIndex = nextPreferredStartDayIndex;
    }

    public int getPrevPreferredStartDayIndex() {
        return prevPreferredStartDayIndex;
    }

    public void setPrevPreferredStartDayIndex(int prevPreferredStartDayIndex) {
        this.prevPreferredStartDayIndex = prevPreferredStartDayIndex;
    }

    // ===================== COMPLEX METHODS ===============================
    public int getCurrOrPrevPreferredStartDayIndex() {
        return (isPreferredSequenceStart() ? dayIndex : prevPreferredStartDayIndex);
    }

    public int getCurrOrNextPreferredStartDayIndex() {
        return (isPreferredSequenceStart() ? dayIndex : nextPreferredStartDayIndex);
    }

    public int getCurrOrPrevPreferredEndDayIndex() {
        return (isPreferredSequenceEnd() ? dayIndex : (isPreferredSequenceStart() ? dayIndex - 1 : prevPreferredStartDayIndex - 1));
    }

    public int getCurrOrNextPreferredEndDayIndex() {
        return (isPreferredSequenceEnd() ? dayIndex : nextPreferredStartDayIndex - 1);
    }

    public boolean isNoNextPreferred() {
        return getNextPreferredStartDayIndex() == noNextPreferredDayIndex;
    }

    public boolean isNoPrevPreferred() {
        return getPrevPreferredStartDayIndex() == noPrevPreferredDayIndex;
    }

    /**
     * @return if this is a preferred start date, then the sequence length that will fill from this date through the next end date; otherwise the days filling the past preferred start date through next end date
     */
    public int getPreferredCoveringLength() {
        if (isPreferredSequenceStart()) {
            return nextPreferredStartDayIndex - dayIndex;
        }
        return nextPreferredStartDayIndex - prevPreferredStartDayIndex;
    }

    /**
     * @return if this is a preferred start boundary, then "today", else day of most recent start boundary
     */
    public DayOfWeek getPreferredStartDayOfWeek() {
        if (isPreferredSequenceStart()) {
            return getDayOfWeek();
        }
        if (isNoPrevPreferred()) {
            throw new IllegalStateException("No prev preferred day of week available for " + toString());
        }
        return date.minusDays(dayIndex - getPrevPreferredStartDayIndex()).getDayOfWeek();
    }

    public DayOfWeek getPreferredEndDayOfWeek() {
        if (isPreferredSequenceEnd()) {
            return getDayOfWeek();
        }
        if (isNoNextPreferred()) {
            throw new IllegalStateException("No next preferred day of week available for " + toString());
        }
        return date.plusDays((getNextPreferredStartDayIndex() - 1) - dayIndex).getDayOfWeek();
    }

    public DayOfWeek getDayOfWeek() {
        return date.getDayOfWeek();
    }

    public int getMostRecentDayIndexOf(DayOfWeek targetDayOfWeek) {
        return dayIndex - getBackwardDaysToReach(targetDayOfWeek);
    }

    public int getUpcomingDayIndexOf(DayOfWeek targetDayOfWeek) {
        return dayIndex + getForwardDaysToReach(targetDayOfWeek);
    }

    public LocalDate getMostRecentDateOf(DayOfWeek targetDayOfWeek) {
        return date.minusDays(getBackwardDaysToReach(targetDayOfWeek));
    }

    public LocalDate getUpcomingDateOf(DayOfWeek targetDayOfWeek) {
        return date.plusDays(getForwardDaysToReach(targetDayOfWeek));
    }

    public int getForwardDaysToReach(DayOfWeek targetDayOfWeek) {
        return getForwardDaysToReach(this.getDayOfWeek(), targetDayOfWeek);
    }

    public int getBackwardDaysToReach(DayOfWeek targetDayOfWeek) {
        return getBackwardDaysToReach(this.getDayOfWeek(), targetDayOfWeek);
    }

    public String getLabel() {
        return date.format(LABEL_FORMATTER);
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
