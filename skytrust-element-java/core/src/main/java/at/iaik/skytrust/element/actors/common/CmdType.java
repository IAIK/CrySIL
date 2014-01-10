package at.iaik.skytrust.element.actors.common;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 5/22/13
 * Time: 8:02 AM
 * To change this template use File | Settings | File Templates.
 */
public enum CmdType {
    decrypt("decrypt"),
    encrypt("encrypt"),
    sign("sign"),
    getKey("getKey"),
    discoverKeys("discoverKeys"),
    authenticate("authenticate"),
    transmitAPDU("transmitAPDU");

    protected String name;

    public String getName() {
        return name;
    }

    CmdType(String name) {
        this.name = name;
    }

}
