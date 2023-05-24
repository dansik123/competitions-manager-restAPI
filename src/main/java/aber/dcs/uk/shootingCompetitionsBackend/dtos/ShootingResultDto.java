package aber.dcs.uk.shootingCompetitionsBackend.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class ShootingResultDto {
    @Min(value = 0, message = "The value must be positive(competitor1)")
    @NotNull(message = "competitor1Result value can't be null(competitor1)")
    @Max(value = 100, message = "The max shooting score value is 100(competitor1)")
    private Integer competitor1Result;
    @Min(value = 0, message = "The value must be positive(competitor2)")
    @NotNull(message = "competitor1Result value can't be null(competitor2)")
    @Max(value = 100, message = "The max shooting score value is 100(competitor2)")
    private Integer competitor2Result;

    public ShootingResultDto() {
    }

    public ShootingResultDto(Integer competitor1Result, Integer competitor2Result) {
        this.competitor1Result = competitor1Result;
        this.competitor2Result = competitor2Result;
    }

    public Integer getCompetitor1Result() {
        return competitor1Result;
    }

    public void setCompetitor1Result(Integer competitor1Result) {
        this.competitor1Result = competitor1Result;
    }

    public Integer getCompetitor2Result() {
        return competitor2Result;
    }

    public void setCompetitor2Result(Integer competitor2Result) {
        this.competitor2Result = competitor2Result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShootingResultDto that = (ShootingResultDto) o;
        return competitor1Result.equals(that.competitor1Result) && competitor2Result.equals(that.competitor2Result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(competitor1Result, competitor2Result);
    }
}
