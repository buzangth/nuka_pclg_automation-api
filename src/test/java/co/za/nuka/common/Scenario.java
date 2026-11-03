package co.za.nuka.common;

public class Scenario {

    private final String name;
    private final Object request;
    private final int expectedStatus;

    public Scenario(String name, Object request, int expectedStatus) {
        this.name = name;
        this.request = request;
        this.expectedStatus = expectedStatus;
    }

    public String getName() {
        return name;
    }

    public Object getRequest() {
        return request;
    }

    public int getExpectedStatus() {
        return expectedStatus;
    }

    @Override
    public String toString() {
        return name;
    }
}
