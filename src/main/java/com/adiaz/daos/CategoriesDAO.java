package com.adiaz.daos;

import java.util.List;

import com.adiaz.entities.Category;

public interface CategoriesDAO extends GenericDAO<Category> {
	public List<Category> findAllCategories();
	public Category findCategoryById(Long id);

}
