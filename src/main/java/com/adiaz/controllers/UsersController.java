package com.adiaz.controllers;

import com.adiaz.entities.Town;
import com.adiaz.entities.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.adiaz.forms.validators.UserFormValidator;
import com.adiaz.services.UsersManager;
import com.adiaz.utils.LocalSportsUtils;

@Controller
@RequestMapping("/users")
public class UsersController {

	private static final Logger logger = Logger.getLogger(UsersController.class);
	@Autowired
	UsersManager usersManager;
	@Autowired
	UserFormValidator userFormValidator;

	@RequestMapping("/list")
	public ModelAndView usersList(
			@RequestParam(value = "update_done", defaultValue = "false") boolean updateDone,
			@RequestParam(value = "add_done", defaultValue = "false") boolean addDone,
			@RequestParam(value = "remove_done", defaultValue = "false") boolean removeDone) {
		ModelAndView modelAndView = new ModelAndView("users_list");
		modelAndView.addObject("users", usersManager.queryAllUsers());
		modelAndView.addObject("remove_done", removeDone);
		modelAndView.addObject("update_done", updateDone);
		modelAndView.addObject("add_done", addDone);
		return modelAndView;
	}

	@RequestMapping("/add")
	public ModelAndView addUser() {
		ModelAndView modelAndView = new ModelAndView("users_add");
		modelAndView.addObject("my_form", new User());
		return modelAndView;
	}

	@RequestMapping("/doAdd")
	public ModelAndView doAddUser(@ModelAttribute("my_form") User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		userFormValidator.validate(user, bindingResult);
		if (!bindingResult.hasErrors()) {
			try {
				user.setPassword(LocalSportsUtils.sha256Encode(user.getPassword01()));
				user.setEnabled(true);
				user.setAccountNonExpired(true);
				Key<Town> key = Key.create(Town.class, user.getTownEntity().getId());
				user.setTownRef(Ref.create(key));
				usersManager.addUser(user);
			} catch (Exception e) {
				logger.error(e);
			}
			String viewName = "redirect:/users/list";
			viewName += "?add_done=true";
			modelAndView.setViewName(viewName);
		} else {
			modelAndView.addObject("my_form", user);
			modelAndView.setViewName("users_add");
		}
		return modelAndView;
	}

	@RequestMapping("/update")
	public ModelAndView updateUser(@RequestParam String userName) {
		ModelAndView modelAndView = new ModelAndView("users_update");
		User user = usersManager.queryUserByName(userName);
		modelAndView.addObject("my_form", user);
		return modelAndView;
	}

	@RequestMapping("/doUpdate")
	public ModelAndView doUpdateUser(@ModelAttribute("my_form") User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		userFormValidator.validate(user, bindingResult);
		if (!bindingResult.hasErrors()) {
			try {
				if (user.isUpdatePassword()) {
					user.setPassword(LocalSportsUtils.sha256Encode(user.getPassword01()));
				} else {
					User originalUser = usersManager.queryUserByName(user.getUsername());
					user.setPassword(originalUser.getPassword());
				}
				Key<Town> key = Key.create(Town.class, user.getTownEntity().getId());
				user.setTownRef(Ref.create(key));
				usersManager.updateUser(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String viewName = "redirect:/users/list";
			viewName += "?update_done=true";
			modelAndView.setViewName(viewName);
		} else {
			modelAndView.addObject("my_form", user);
			modelAndView.setViewName("users_update");
		}
		return modelAndView;
	}

	@RequestMapping("/doDelete")
	public ModelAndView doDelete(@RequestParam String userName) {
		ModelAndView modelAndView = new ModelAndView();
		String viewName = "redirect:/users/list";
		try {
			usersManager.removeUser(userName);
			viewName += "?remove_done=true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		modelAndView.setViewName(viewName);
		return modelAndView;
	}

	@RequestMapping("/view")
	public ModelAndView view(@RequestParam(value = "userName") String userName) throws Exception {
		ModelAndView modelAndView = new ModelAndView("users_view");
		User user = usersManager.queryUserByName(userName);
		modelAndView.addObject("my_form", user);
		return modelAndView;
	}

}
