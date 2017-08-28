import org.crysil.communications.http.GateKeeperConfiguration;
import org.crysil.errorhandling.AuthenticationFailedException;
import org.testng.annotations.Test;

public class TestTest {

	@Test
	public void gettingStarted() throws AuthenticationFailedException {
		GateKeeperConfiguration DUT = new GateKeeperConfiguration();
		DUT.getAuthProcess(null, null);
	}
}
