package com.adiaz.controllers;

import com.adiaz.entities.*;
import com.adiaz.forms.IssuesForm;
import com.adiaz.forms.MatchForm;
import com.adiaz.forms.TeamFilterForm;
import com.adiaz.forms.TownForm;
import com.adiaz.services.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("server")
public class RESTController {

	@Autowired
	SportsManager sportsManager;
	@Autowired
	CategoriesManager categoriesManager;
	@Autowired
	CompetitionsManager competitionsManager;
	@Autowired
	MatchesManager matchesManager;
	@Autowired
	ClassificationManager classificationManager;
	@Autowired
	CourtManager courtManager;
	@Autowired
	TeamManager teamManager;
	@Autowired
	IssuesManager issuesManager;
	@Autowired
	TownManager townManager;

	private static final Logger logger = Logger.getLogger(RESTController.class);

	@RequestMapping(value = "/sports/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Sport> getSportsById(@PathVariable("id") long id) {
		ResponseEntity<Sport> response;
		Sport sport = sportsManager.querySportsById(id);
		if (sport == null) {
			response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			response = new ResponseEntity<>(sport, HttpStatus.OK);
		}
		return response;
	}

	@RequestMapping(value = "/sports_name/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Sport> getSportsById(@PathVariable("name") String name) {
		ResponseEntity<Sport> response;
		Sport sport = sportsManager.querySportsByName(name);
		if (sport == null) {
			response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			response = new ResponseEntity<>(sport, HttpStatus.OK);
		}
		return response;
	}

	@RequestMapping(value = "/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Category> getCategories() {
		List<Category> queryCategories = categoriesManager.queryCategories();
		return queryCategories;
	}

	@RequestMapping(value = "/competitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Competition> competitions() {
		List<Competition> competitions = competitionsManager.queryCompetitions();
		return competitions;
	}

	@RequestMapping(value = "/competitions/{competition_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Competition competitions(@PathVariable("competition_id") Long competitionId) {
		Competition competition = competitionsManager.queryCompetitionsByIdEntity(competitionId);
		return competition;
	}

	@RequestMapping(value = "/search_competitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Competition> searchCompetitions(
			@RequestParam(value = "idTown") Long idTown,
			@RequestParam(value = "onlyPublished", required = false, defaultValue = "true") Boolean onlyPublised) {
		List<Competition> competitions = competitionsManager.queryCompetitionsByTown(idTown, onlyPublised);
		return competitions;
	}

	@RequestMapping(value = "/search_sports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Sport> searchCompetitions(
			@RequestParam(value = "idTown") Long idTown) {
		TownForm townForm = townManager.queryById(idTown);
		return townForm.formToEntity().getSportsDeref();
	}

	@RequestMapping(value = "/matches", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Match> getMatches() {
		List<Match> matches = matchesManager.queryMatches();
		return matches;
	}

	class CompetitionDetails {
		private Long lastPublished;
		private List<Match> matches;
		private List<ClassificationEntry> classification;
		private List<String> weeksNames;

		public List<Match> getMatches() {
			return matches;
		}

		public void setMatches(List<Match> matches) {
			this.matches = matches;
		}

		public List<ClassificationEntry> getClassification() {
			return classification;
		}

		public void setClassification(List<ClassificationEntry> classification) {
			this.classification = classification;
		}

		public Long getLastPublished() {
			return lastPublished;
		}

		public void setLastPublished(Long lastPublished) {
			this.lastPublished = lastPublished;
		}

        public List<String> getWeeksNames() {
            return weeksNames;
        }

        public void setWeeksNames(List<String> weeksNames) {
            this.weeksNames = weeksNames;
        }
    }

	@RequestMapping(value = "/competitiondetails/{competition_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CompetitionDetails getMatchesAndClassification(@PathVariable("competition_id") Long competitionId){
		Competition competition = competitionsManager.queryCompetitionsByIdEntity(competitionId);
		List<Match> matches = matchesManager.queryMatchesByCompetitionPublished(competitionId);
		List<ClassificationEntry> classificationEntries = classificationManager.queryClassificationByCompetition(competitionId);
		CompetitionDetails competitionDetails = new CompetitionDetails();
		competitionDetails.matches = matches;
		competitionDetails.classification = classificationEntries;
		competitionDetails.weeksNames = competition.getWeeksNames();
		if (competition.getLastPublished()!=null) {
			competitionDetails.setLastPublished(competition.getLastPublished().getTime());
		}
		return competitionDetails;
	}

	@RequestMapping(value = "/matches/{competition_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Match> getMatches(@PathVariable("competition_id") Long competitionId) {
		List<Match> matches = matchesManager.queryMatchesByCompetitionPublished(competitionId);
		return matches;
	}

	@RequestMapping(value = "/classification/{competition_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ClassificationEntry> getClassification(@PathVariable("competition_id") Long competitionId) {
		List<ClassificationEntry> classificationList = classificationManager.queryClassificationByCompetition(competitionId);
		return classificationList;
	}

	@RequestMapping(value = "/sports", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Sport> listSports() {
		List<Sport> sportsList = sportsManager.querySports();
		return sportsList;
	}

	@RequestMapping(value = "/match/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MatchForm> getMatch(@PathVariable("id") Long id) {
		Match match = matchesManager.queryMatchesById(id);
		if (match == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		MatchForm matchForm = new MatchForm(match);
		return new ResponseEntity<>(matchForm, HttpStatus.OK);
	}

	// TODO: 10/07/2017 IMPORTAN protect this call in production environment. 
	@RequestMapping(value = "/match/{id}", method = RequestMethod.PUT)
	public ResponseEntity<MatchForm> updateMatchScore(@PathVariable("id") Long id, @RequestBody MatchForm matchForm) {
		boolean updated;
		try {
			updated = matchesManager.update(matchForm);
		} catch (Exception e) {
			logger.error(e.getMessage() , e);
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
		if (updated) {
			return new ResponseEntity<>(matchForm, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
	}

	@RequestMapping(value = "/issues", method = RequestMethod.POST)
	public ResponseEntity<Long> createIssue(@RequestBody IssuesForm issuesForm) {
		Long issueId = -1L;
		if (!issuesManager.reachMaxIssuesPerDay()) {
			if (issuesForm!=null
					&& StringUtils.isNotBlank(issuesForm.getClientId())
					&& issuesManager.allowUserToReportIssue(issuesForm.getClientId())){
				try {
					if (issuesManager.isValidIssue(issuesForm)) {
						Competition competition = competitionsManager.queryCompetitionsByIdEntity(issuesForm.getCompetitionId());
						issuesForm.setTownId(competition.getTownEntity().getId());
						issuesForm.setDateSent(new Date());
						issueId = issuesManager.addIssue(issuesForm);
					}
				} catch (Exception e) {
					logger.error("error creating issue", e);
				}
			}
		}
		ResponseEntity<Long> response;
		if (issueId==-1) {
			response = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		} else {
			response = new ResponseEntity<>(issueId, HttpStatus.OK);
		}
		return response;
	}

	@RequestMapping(value = "/courts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Court> courts(
			@RequestParam(value = "idTown") Long idTown,
			@RequestParam(value = "idSport") Long idSport) {
		if (idTown!=null && idSport!=null) {
			return courtManager.querySportCourtsByTownAndSport(idTown, idSport);
		}
		return null;
	}

	@RequestMapping(value = "/teams", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Team> teams(
			@RequestParam(value = "idTown") Long idTown,
			@RequestParam(value = "idSport") Long idSport,
			@RequestParam(value = "idCategory") Long idCategory) {
		TeamFilterForm teamFilterForm = new TeamFilterForm(idTown, idSport, idCategory);
		return teamManager.queryByFilter(teamFilterForm);
	}

	@RequestMapping(value = "/towns", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Town> towns(){
		return townManager.queryActives();
	}


	@RequestMapping (value = "/competitionsInTown/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Competition> competitionsInTown(@PathVariable("id") Long idTown){
		return competitionsManager.queryCompetitionsByTown(idTown, false);
	}


}