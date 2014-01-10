package at.iaik.skytrust.element.actors.apdu;

import at.gv.egiz.smcc.util.SmartCardIO;
import at.iaik.skytrust.element.actors.Actor;
import at.iaik.skytrust.element.actors.common.BasicCommand;
import at.iaik.skytrust.element.actors.common.CmdFactory;
import at.iaik.skytrust.element.actors.common.CmdType;
import at.iaik.skytrust.element.authentication.KeyBean;
import at.iaik.skytrust.element.skytrustprotocol.old.CMDPacket;
import com.google.gson.Gson;

import javax.smartcardio.*;
import javax.smartcardio.CardTerminals.State;
import java.util.*;

public class APDUActor implements Actor {
    protected Map<CmdType, BasicCommand> _providedCommands;
    protected static Card _cardInUse = null;
    private Object lock = new Object();

    public APDUActor() {
        createCommandEntries();
    }

    @Override
    public byte[] take(byte[] command) {

        // ------------------DEBUG INFO--------------------------------------------
    /*System.out.println("APDUProvider: Command received at: "
        + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        .format(new Date()) + " #################");
    System.out.println(new String(command));
    System.out
    .println("#######################################################");*/
        // ------------------DEBUG INFO--------------------------------------------

        return forwardCommand(command);
    }

    @Override
    public Set<CmdType> getProvidedCommands() {
        return _providedCommands.keySet();
    }

    //TODO adapt to new actor interface
    @Override
    public List<KeyBean> getAvailableKeys(String userId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //TODO adapt to new actor interface
    @Override
    public boolean isKeyAvailable(String userId, String keyId) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

//    @Override
//    public List<KeyBean> getAvailableKeys() {
//        return null;
//    }
//
//    @Override
//    public boolean isKeyAvailable(String friendlyname) {
//        return true;
//    }

    private void createCommandEntries(){
        System.out.println("APDUProvider: Create Commands");

        _providedCommands = new HashMap<CmdType, BasicCommand>();

        BasicCommand transmitAPDU = new APDUCommandTransmit(this);
        _providedCommands.put(transmitAPDU.getCommandType(), transmitAPDU);
    }

    private byte[] forwardCommand(byte[] command) {
        Gson gson = new Gson();
        CMDPacket cmd = gson.fromJson(new String(command), CMDPacket.class);

        System.out.println("Command - " + cmd.getPayload().getCommand());
        if (_providedCommands.containsKey(CmdType.valueOf(cmd.getPayload().getCommand()))) {
            byte[] resp = _providedCommands.get(CmdType.valueOf(cmd.getPayload().getCommand())).handle(command);

            if (resp != null)
                return resp;
        }

        CMDPacket response = CmdFactory.createStandardResponse(cmd);
        response.getPayload().setCode(400);
        return response.getBytes();
    }

    private boolean initializeCard() {
        if (_cardInUse != null) {
            try {
                _cardInUse.disconnect(true);
            } catch (CardException e) {
                e.printStackTrace();
            }
        }

        _cardInUse = null;

        CardTerminals terminals = new SmartCardIO().getCardTerminals();
        if (terminals == null) {
            System.err.println("No smartcard terminals available.");
            return false;
        }

        try {
            for (CardTerminal terminal : terminals.list(State.CARD_PRESENT)) {
                _cardInUse = null;
                try {
                    System.out.println("Connect to card in terminal " + terminal.getName());
                    _cardInUse = terminal.connect("*");

                    // Get the ATR for demo purposes
          /*ATR atr = _cardInUse.getATR();
          System.out.print("ATR = 0x");
          for (byte element : atr.getBytes())
            System.out.printf("%02X ",element);*/

                    return true;
                } catch (CardException e) {
                    System.out.println("Failed to connect to card.");
                }
            }
        } catch (CardException e) {
            e.printStackTrace();
        }

        return false;
    }

    public byte[] transmit(byte[] load) {
        synchronized (lock) {
            if (_cardInUse == null) {
                boolean successful = initializeCard();

                if (!successful)
                {
                    System.err.println("transmit: No smartcard available!");
                    return null;
                }
            }

            CardChannel ch = _cardInUse.getBasicChannel();

            byte[] resp = null;
            if (Arrays.equals(load, new String("getATR").getBytes())) {
                resp = _cardInUse.getATR().getBytes();
        /*} else if (Arrays.equals(load, new String("reset").getBytes())) {
        initializeCard();

        if (_cardInUse != null)
          resp = _cardInUse.getATR().getBytes();*/
            } else {
                try {
                    ResponseAPDU respAPDU = ch.transmit(new CommandAPDU(load));
                    resp = respAPDU.getBytes();
                } catch (CardException e) {
                    e.printStackTrace();

                    // On exception, reinitialize and try again
                    initializeCard();
                    if (_cardInUse != null) {
                        try {
                            ResponseAPDU respAPDU = ch.transmit(new CommandAPDU(load));
                            resp = respAPDU.getBytes();
                        } catch (Exception f) {
                            f.printStackTrace();
                        }
                    }
                }
            }

            return resp;
        }
    }
}