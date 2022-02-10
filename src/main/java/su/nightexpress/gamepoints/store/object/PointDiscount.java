package su.nightexpress.gamepoints.store.object;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.gamepoints.api.store.IPointDiscount;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public class PointDiscount implements IPointDiscount {

    private       int                              amount;
    private final Map<DayOfWeek, Set<LocalTime[]>> times;

    public PointDiscount(int amount, @NotNull Map<DayOfWeek, Set<LocalTime[]>> times) {
        this.setAmount(amount);
        this.times = times;
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(PLACEHOLDER_AMOUNT, String.valueOf(this.getAmount()))
            ;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @NotNull
    @Override
    public Map<DayOfWeek, Set<LocalTime[]>> getTimes() {
        return times;
    }
}
