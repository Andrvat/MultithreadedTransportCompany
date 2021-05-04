import lombok.Builder;

@Builder
public class Good {
    private final String goodName;

    public String getGoodName() {
        return goodName;
    }
}
