package edu.ksu.canvas.aviation.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import edu.ksu.canvas.aviation.factory.SectionModelFactory;
import edu.ksu.canvas.aviation.form.RosterForm;
import edu.ksu.canvas.aviation.form.RosterFormValidator;
import edu.ksu.canvas.aviation.util.DropDownOrganizer;
import edu.ksu.canvas.error.InvalidInstanceException;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.canvas.error.OauthTokenRequiredException;
import edu.ksu.lti.model.LtiSession;


@Controller
@Scope("session")
@SessionAttributes("rosterForm")
@RequestMapping("/roster")
public class RosterController extends AviationBaseController {
    
    private static final Logger LOG = Logger.getLogger(RosterController.class);
    
    @Autowired
    private SectionModelFactory sectionModelFactory;
    
    @Autowired
    private RosterFormValidator validator;

    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
    
    @RequestMapping()
    public ModelAndView roster(@RequestParam(required = false) Date date) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        return roster(date, null);
    }
  
    @RequestMapping("/{sectionId}")
    public ModelAndView roster(@RequestParam(required = false) Date date, @PathVariable String sectionId) throws NoLtiSessionException, OauthTokenRequiredException, InvalidInstanceException, IOException {
        ltiLaunch.ensureApiTokenPresent(getApplicationName()); //should be present on each call
        SectionState sectionState = getSectionState(sectionId);
        sectionId = sectionState.selectedSection.getCanvasSectionId().toString();
        
        //Sets the date to today if not already set
        if (date == null){
            date = new Date();
        }
        RosterForm rosterForm = new RosterForm();
        rosterForm.setCurrentDate(date);
        rosterForm.setSectionModels(sectionModelFactory.createSectionModels(sectionState.sections));
        persistenceService.loadCourseInfoIntoForm(rosterForm, sectionState.selectedSection.getCanvasCourseId());
        persistenceService.loadAttendanceIntoRoster(rosterForm, date);
        
        ModelAndView page = new ModelAndView("roster");
        page.addObject("rosterForm", rosterForm);
        page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sectionState.sections, sectionId));
        page.addObject("selectedSectionId", sectionId);
        
        return page;
    }
    
    @RequestMapping(value= "/{sectionId}/save", params = "changeDate", method = RequestMethod.POST)
    public ModelAndView changeDate(@PathVariable String sectionId, @ModelAttribute("rosterForm") RosterForm rosterForm) throws IOException, NoLtiSessionException {
        return roster(rosterForm.getCurrentDate(), sectionId);
    }
    
    @RequestMapping(value = "/{sectionId}/save", params = "saveAttendance", method = RequestMethod.POST)
    public ModelAndView saveAttendance(@PathVariable String sectionId, @ModelAttribute("rosterForm") @Valid RosterForm rosterForm, BindingResult bindingResult) throws IOException, NoLtiSessionException {
        validator.validate(rosterForm, bindingResult);
        
        if (bindingResult.hasErrors()) {
            ModelAndView page = new ModelAndView("/roster");
            page.addObject("error", "Please check all sections when correcting user input. Then try saving again.");
            SectionState sectionState = getSectionState(sectionId);
            page.addObject("sectionList", DropDownOrganizer.sortWithSelectedSectionFirst(sectionState.sections, sectionId));
            page.addObject("selectedSectionId", sectionId);
            return page;
        } else {
            LtiSession ltiSession = ltiLaunch.getLtiSession();
            LOG.info("Attempting to save section attendance for section : " + sectionId + " User: " + ltiSession.getEid());

            persistenceService.saveClassAttendance(rosterForm);
            return roster(rosterForm.getCurrentDate(), sectionId);
        }
    }
    
}