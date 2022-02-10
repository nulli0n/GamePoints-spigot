package su.nightexpress.gamepoints.api.store;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.IPlaceholder;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public interface IPointDiscount extends IPlaceholder {

    String PLACEHOLDER_AMOUNT = "%discount_amount%";

    int getAmount();

    void setAmount(int amount);

    @NotNull Map<DayOfWeek, Set<LocalTime[]>> getTimes();

    default boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        Set<LocalTime[]> times = this.getTimes().get(now.getDayOfWeek());
        if (times == null || times.isEmpty()) return false;

        LocalTime timeNow = now.toLocalTime();
        return times.stream().anyMatch(time -> timeNow.isAfter(time[0]) && timeNow.isBefore(time[1]));
    }
}
