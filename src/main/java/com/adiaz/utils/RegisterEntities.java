package com.adiaz.utils;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.List;

import com.adiaz.entities.*;
import com.adiaz.forms.TownForm;
import com.adiaz.services.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.adiaz.entities.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;

public class RegisterEntities {

	private static final Logger logger = Logger.getLogger(RegisterEntities.class.getName());
	
	@Autowired SportsManager sportsManager;
	@Autowired CategoriesManager categoriesManager;
	@Autowired CompetitionsManager competitionsManager;
	@Autowired UsersManager usersManager;
	@Autowired SportCenterManager sportCenterManager;
	@Autowired SportCourtManager sportCourtManager;
	@Autowired MatchesManager matchesManager;
	@Autowired ClassificationManager classificationManager;
	@Autowired TownManager townManager;
	
	public void init() throws Exception {
		ObjectifyService.register(Sport.class);
		ObjectifyService.register(Category.class);
		ObjectifyService.register(Competition.class);
		ObjectifyService.register(Match.class);
		ObjectifyService.register(ClassificationEntry.class);
		ObjectifyService.register(User.class);
		ObjectifyService.register(SportCenter.class);
		ObjectifyService.register(SportCourt.class);
		ObjectifyService.register(Town.class);

		/** clean DB. */
		logger.debug("DB clean");
		sportsManager.removeAll();
		categoriesManager.removeAll();
		matchesManager.removeAll();
		classificationManager.removeAll();
		competitionsManager.removeAll();
		usersManager.removeAll();
		sportCourtManager.removeAll();
		sportCenterManager.removeAll();
		townManager.removeAll();

		/** load sports */
		 Key<Sport> keySportBasket = null;
		 Key<Category> keyCategories = null;
		for (String sportName : ConstantsLegaSport.SPORTS_NAMES) {
			Key<Sport> key = ofy().save().entity(new Sport(sportName)).now();
			if ("BALONCESTO".equals(sportName)) {
				keySportBasket = key;
			}
		}
		
		/** load categories */
		String[] categoriesNames = ConstantsLegaSport.CATEGORIES_NAMES;
		int order = 0;
		for (String name : categoriesNames) {
			Category category = new Category();
			category.setName(name);
			category.setOrder(order++);
			keyCategories = ofy().save().entity(category).now();
		}
		/** load competitions */
		Competition competition = new Competition();
		competition.setName("liga division honor");
		competition.setCategory(Ref.create(keyCategories));
		competition.setSport(Ref.create(keySportBasket));
		competitionsManager.add(competition);
		
		List<Match> matchesList = UtilsLegaSport.parseCalendar(competition);
		try {
			matchesManager.add(matchesList);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		try {
			String classificationStr = UtilsLegaSport.classificationStr();
			List<ClassificationEntry> classificationList = UtilsLegaSport.parseClassification(classificationStr, competition.getId());
			classificationManager.add(classificationList);
		} catch (IOException e) {
			e.printStackTrace();
		}

		TownForm townForm = new TownForm();
		townForm.setName("Leganés");
		townForm.setActive(true);
		Long townId = townManager.add(townForm);

		townForm = new TownForm();
		townForm.setName("Fuenlabrada");
		townForm.setActive(true);
		townManager.add(townForm);

		/** load users */
		String name = "antonio.diaz";
		String password = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
		usersManager.addUser(initUser(name, password, true));

		name = "user.lega";
		password ="8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
		usersManager.addUser(initUser(name, password, false), townId);





		SportCenter sportsCenter = new SportCenter();
		sportsCenter.setName("Pabellon Europa");
		sportsCenter.setAddress("Av. de Alemania, 2, 28916 Leganés, Madrid");
		Key<Town> townKey = Key.create(Town.class, townId);
		sportsCenter.setTownRef(Ref.create(townKey));
		Key<SportCenter> centerKey = ofy().save().entity(sportsCenter).now();
		
		SportCourt court = new SportCourt();
		court.setName("Pista central");
		court.setCenter(Ref.create(centerKey));
		court.getSports().add(Ref.create(keySportBasket));
		ofy().save().entity(court).now();

		List<SportCenter> list = ofy().load().type(SportCenter.class).list();
		for (SportCenter sportCenter : list) {
			Key<SportCenter> keyCenter = Key.create(SportCenter.class, sportCenter.getId());
			Ref<SportCenter> refCenter = Ref.create(keyCenter);
			ofy().load().type(SportCourt.class).filter("center", refCenter).list();
		}



		logger.debug("finished init...");
	}

	private User initUser(String name, String password, boolean isAdmin) {
		User user = new User();
		user.setUsername(name);
		user.setPassword(password);
		user.setAdmin(isAdmin);
		user.setBannedUser(false);
		user.setEnabled(true);
		user.setAccountNonExpired(true);
		return user;
	}
}
