package k.m;

/**
 * Created by as on 17-6-26.
 */

public abstract class PayListener implements IPayListener {
    private String mId;

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }
}
