package com.adiaz.daos;


import com.adiaz.entities.SportCenter;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/** Created by toni on 11/07/2017. */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:web/WEB-INF/applicationContext-testing.xml")
@WebAppConfiguration("file:web")
public class SportCenterDAOImplTest {

    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private static final String SPORTCENTER_NAME_1 = "PABELLON EUROPA";
    private static final String SPORTCENTER_NAME_2 = "LA CANTERA";
    private static final String SPORTCENTER_ADDRESS_1 = "Address 01";
    private static final String SPORTCENTER_ADDRESS_2 = "Address 02";



    @Autowired
    private SportCenterDAO sportCenterDAO;

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        ObjectifyService.register(SportCenter.class);
    }

    @After
    public void tearDown() throws Exception {
        helper.tearDown();
    }

    @Test
    public void create() throws Exception {
        Assert.assertEquals(0, sportCenterDAO.findAllSportsCenters().size());
        Key<SportCenter> key = createSportCenter();
        Assert.assertEquals(1, sportCenterDAO.findAllSportsCenters().size());
        SportCenter sportCenter = sportCenterDAO.findSportsCenterById(key.getId());
        Assert.assertEquals(key.getId(), (long)sportCenter.getId());
        Assert.assertEquals(SPORTCENTER_NAME_1, sportCenter.getName());
        Assert.assertEquals(SPORTCENTER_ADDRESS_1, sportCenter.getAddress());
    }

    @Test
    public void update() throws Exception {
        Key<SportCenter> key = createSportCenter();
        SportCenter sportCenter = sportCenterDAO.findSportsCenterById(key.getId());
        sportCenter.setName(SPORTCENTER_NAME_2);
        sportCenter.setAddress(SPORTCENTER_ADDRESS_2);
        sportCenterDAO.update(sportCenter);
        sportCenter = sportCenterDAO.findSportsCenterById(key.getId());
        Assert.assertEquals(key.getId(), (long)sportCenter.getId());
        Assert.assertEquals(SPORTCENTER_NAME_2, sportCenter.getName());
        Assert.assertEquals(SPORTCENTER_ADDRESS_2, sportCenter.getAddress());
    }


    @Test
    public void removeByItem() throws Exception {
        Key<SportCenter> key = createSportCenter();
        SportCenter sportCenter = sportCenterDAO.findSportsCenterById(key.getId());
        sportCenterDAO.remove(sportCenter.getId());
        Assert.assertTrue(sportCenterDAO.findSportsCenterById(sportCenter.getId())==null);
    }

    @Test
    public void removeById() throws Exception {
        Key<SportCenter> key = createSportCenter();
        SportCenter sportCenter = sportCenterDAO.findSportsCenterById(key.getId());
        sportCenterDAO.remove(sportCenter.getId());
        Assert.assertTrue(sportCenterDAO.findSportsCenterById(sportCenter.getId())==null);
    }

    @Test
    public void findAllSportsCenters() throws Exception {
        createSportCenter();
        createSportCenter();
        Assert.assertEquals(2, sportCenterDAO.findAllSportsCenters().size());
    }

    @Test
    public void findSportsCenterById() throws Exception {
        Key<SportCenter> key = createSportCenter();
        SportCenter sportCenter = sportCenterDAO.findSportsCenterById(key.getId());
        Assert.assertEquals(key.getId(), (long) sportCenter.getId());
    }

    private Key<SportCenter> createSportCenter() throws Exception {
        SportCenter sportCenter = new SportCenter();
        sportCenter.setName(SPORTCENTER_NAME_1);
        sportCenter.setAddress(SPORTCENTER_ADDRESS_1);
        return sportCenterDAO.create(sportCenter);
    }
}