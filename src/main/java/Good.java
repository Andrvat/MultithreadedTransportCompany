import lombok.Builder;

import java.util.UUID;

@Builder
public class Good {
    private final String goodName;

    public String getGoodName() {
        return goodName;
    }
}
