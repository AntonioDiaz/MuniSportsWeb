package com.adiaz.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.adiaz.daos.CompetitionsDAO;
import com.adiaz.entities.Competition;
import com.adiaz.entities.SportCenterCourt;
import com.adiaz.forms.GenerateCalendarForm;
import com.adiaz.utils.RegisterEntities;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adiaz.daos.MatchesDAO;
import com.adiaz.entities.Match;


@Service ("matchesManager")
public class MatchesManagerImpl implements MatchesManager {

	private static final Logger logger = Logger.getLogger(MatchesManagerImpl.class.getName());

	@Autowired
	MatchesDAO matchesDAO;
	@Autowired
	CompetitionsDAO competitionsDAO;
	@Autowired
	SportCenterCourtManager sportCenterCourtManager;


	@Override
	public Long add(Match match) throws Exception {
		return matchesDAO.create(match).getId();
	}

	@Override
	public Long addPublishedAndWorkingcopy(Match match) throws Exception {
		match.setWorkingCopy(false);
		Long idPublisedCopy = this.add(match);
		Key<Match> matchKey = Key.create(Match.class, idPublisedCopy);
		match.setId(null);
		match.setWorkingCopy(true);
		match.setMatchPublishedRef(Ref.create(matchKey));
		Long idWorkingCopy = this.add(match);
		return idWorkingCopy;
	}

	@Override
	public boolean remove(Match match) throws Exception {
		return matchesDAO.remove(match);
	}

	@Override
	public boolean update(Match match) throws Exception {
		return matchesDAO.update(match);
	}

	@Override
	public void updatePublishedMatches(Long idCompetition) throws Exception {
		List<Match> matches = queryMatchesByCompetitionWorkingCopy(idCompetition);
		for (Match match : matches) {
			// TODO: 22/07/2017 optimize this method, check if it is necessary to update.
			Match published = match.getMatchPublished();
			published.setScoreLocal(match.getScoreLocal());
			published.setScoreVisitor(match.getScoreVisitor());
			published.setDate(match.getDate());
			published.setSportCenterCourtRef(match.getSportCenterCourtRef());
			//// TODO: 22/07/2017 optimize this method, could be better to send a list of entities to update 
			update(published);
		}
	}

	@Override
	public boolean checkUpdatesToPublish(Long idCompetition) throws Exception {
		List<Match> matches = queryMatchesByCompetitionWorkingCopy(idCompetition);
		for (Match match : matches) {
			if (match.isUpdatedScore() || match.isUpdatedDate() || match.isUpdatedCourt()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Match> queryMatchesByCompetitionWorkingCopy(Long competitionId) {
		return matchesDAO.findByCompetition(competitionId, true);
	}

	@Override
	public List<Match> queryMatchesByCompetitionPublished(Long competitionId) {
		return matchesDAO.findByCompetition(competitionId, false);
	}

	@Override
	public void addMatchListAndPublish(List<Match> matchesList) throws Exception {
		for (Match match : matchesList) {
			addPublishedAndWorkingcopy(match);
		}
	}

	@Override
	public List<Match> queryMatches() {
		return matchesDAO.findAll();
	}

	@Override
	public Match queryMatchesById(Long id) {
		return matchesDAO.findById(id);
	}

	@Override
    public Integer howManyWeek(List<Match> matchesList) {
        Set<Integer> diferentsWeeks = new HashSet<>();
        for (Match match : matchesList) {
            diferentsWeeks.add(match.getWeek());
        }
        return diferentsWeeks.size();
    }

    @Override
	public void removeAll() throws Exception {
		List<Match> queryAllMatches = matchesDAO.findAll();
		for (Match match : queryAllMatches) {
			matchesDAO.remove(match);
		}		
	}

	@Override
	public void generateCalendar(GenerateCalendarForm form) throws Exception {
		Competition competition = competitionsDAO.findCompetitionsById(form.getIdCompetition());
		Ref<Competition> competitionRef = Ref.create(competition);
		SportCenterCourt court = sportCenterCourtManager.querySportCourt(form.getIdCourt());
		Ref<SportCenterCourt> courtRef = Ref.create(court);
		int weeks = form.getNumTeams() * 2 - 2;
		int matchesEachWeek = form.getNumTeams() / 2;
		for (int i = 0; i < weeks; i++) {
			for (int j = 0; j < matchesEachWeek; j++) {
				Match match = new Match();
				match.setCompetitionRef(competitionRef);
				match.setSportCenterCourtRef(courtRef);
				match.setWeek(i+1);
				addPublishedAndWorkingcopy(match);
			}
		}
	}
}
