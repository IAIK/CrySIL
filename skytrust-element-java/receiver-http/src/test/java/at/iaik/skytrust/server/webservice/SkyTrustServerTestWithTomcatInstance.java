package at.iaik.skytrust.server.webservice;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.codehaus.plexus.util.FileUtils;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 9/11/13
 * Time: 7:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class SkyTrustServerTestWithTomcatInstance {

    /** The tomcat instance. */
    private Tomcat mTomcat;
    /** The temporary directory in which Tomcat and the app are deployed. */
    private String mWorkingDir = System.getProperty("java.io.tmpdir");

    @Before
    public void setup() throws Throwable {
        mTomcat = new Tomcat();
        mTomcat.setPort(0);
        mTomcat.setBaseDir(mWorkingDir);
        mTomcat.getHost().setAppBase(mWorkingDir);
        mTomcat.getHost().setAutoDeploy(true);
        mTomcat.getHost().setDeployOnStartup(true);
        String contextPath = "/" + "SkyTrustServer";
        File webApp = new File(mWorkingDir,"SkyTrustServer");
        File oldWebApp = new File(webApp.getAbsolutePath());
        FileUtils.deleteDirectory(oldWebApp);
        new ZipExporterImpl(createWebArchive()).exportTo(new File(mWorkingDir + "/" + "SkyTrustServer" + ".war"), true);
        mTomcat.addWebapp(mTomcat.getHost(), contextPath, webApp.getAbsolutePath());
        mTomcat.start();

    }

    protected WebArchive createWebArchive() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, "SkyTrustServer.war");
        File warFileLocation = new File("./target/SkyTrustServer.war");
        archive.merge(ShrinkWrap.create(GenericArchive.class).as(ZipImporter.class)
                .importFrom(warFileLocation).as(GenericArchive.class),
                "/", Filters.includeAll());
        return archive;
    }

    @Test
    public void testHelloWorld() {
//        SRequest request = new SRequest();
//
//        SkyTrustHeader header = new SkyTrustHeader();
//        header.setSessionId("42");
//        header.setCommandId("123");
//        header.setProtocolVersion("1.0");
//        request.setHeader(header);
//
//        SkyTrustRequestPayload payload = new SkyTrustRequestPayload();
//
//        SAlgorithm algorithm = new SAlgorithm();
//        payload.setAlgorithm(algorithm);
//
//        SCryptoParams skyTrustKey = new SCryptoParams();
//        skyTrustKey.setId("skytrust-test-key");
//        skyTrustKey.setKeyType("handle");
//        payload.setKey(skyTrustKey);
//        payload.setCommand("getKey");
//        request.setPayload(payload);
//
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
//        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//
//
//        SkyTrustResponse skyTrustResponse = restTemplate.postForObject("http://localhost:8080/SkyTrustServer/rest/json",request,SkyTrustResponse.class);
//
//        try {
//            String requestJson = ow.writeValueAsString(request);
//            System.out.println(requestJson);
//
//            String responseJson = ow.writeValueAsString(skyTrustResponse);
//            System.out.println(responseJson);
//
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

    }

    protected int getTomcatPort() {
        return mTomcat.getConnector().getLocalPort();
    }

    @After
    public final void teardown() throws Throwable {
        if (mTomcat.getServer() != null
                && mTomcat.getServer().getState() != LifecycleState.DESTROYED) {
            if (mTomcat.getServer().getState() != LifecycleState.STOPPED) {
                mTomcat.stop();
            }
            mTomcat.destroy();
        }
    }

}
