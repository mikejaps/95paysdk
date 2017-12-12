package k.m;

public interface IStub {

    public void pay(PayTask payTask);

    public PayTask getCurPayTask();

    public void startService();

    public void stopService();

    public int getVersion();

    public boolean isRunning();

    public void setPayloadVersion(String verion);

    public String getPayloadVersion();

}
