package org.crysil.gatekeeperwithsessions.configuration;

/**
 * The Class Operation.
 */
public class Operation extends Feature {

    /** The operation. */
    private final String operation;

    /**
     * Instantiates a new operation.
     *
     * @param operation
     *            the operation
     */
    public Operation(String operation) {
        this.operation = operation;
    }

    /**
     * Gets the operation.
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "=" + getOperation();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Operation other = (Operation) obj;
        if (operation == null) {
            if (other.operation != null)
                return false;
        } else if (!operation.equals(other.operation))
            return false;
        return true;
    }

}
