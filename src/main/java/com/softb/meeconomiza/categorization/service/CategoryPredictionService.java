package com.softb.meeconomiza.categorization.service;

import com.softb.meeconomiza.categorization.model.CategoryPrediction;
import com.softb.meeconomiza.categorization.model.SanitizePattern;
import com.softb.meeconomiza.categorization.model.SubCategory;
import com.softb.meeconomiza.categorization.repository.CategoryPredictionRepository;
import com.softb.meeconomiza.categorization.repository.SanitizePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Put together all services for related to predicting category
 */
@Service
public class CategoryPredictionService {


    @Autowired
    private CategoryPredictionRepository categoryPredictionRepository;


    @Autowired
    private SanitizePatternRepository sanitizePatternRepository;

    /**
     * Predict the correct category for the informed description
     * @param description Description to be categorized
     * @param groupId Category's owner
     * @return
     */
    public SubCategory getCategoryForDescription(String description, Integer groupId){
        CategoryPrediction prediction = categoryPredictionRepository.getByDescription(sanitize(description), groupId);
        return (prediction != null ? prediction.getSubCategory() : null);
    }

    /**
     * Register a use or rejection of a category's prediction.
     * @param description
     * @param subCategory
     * @param groupId
     */
    public void register(String description, SubCategory subCategory, Integer groupId){
        String sanitizedDescription = sanitize(description);

        CategoryPrediction categoryPrediction = categoryPredictionRepository.getByDescription(sanitizedDescription, groupId);

        if (categoryPrediction == null){
            categoryPrediction = new CategoryPrediction(sanitizedDescription, subCategory, 1, 0, groupId);
        } else {
            if (categoryPrediction.getSubCategory().equals(subCategory)){
                categoryPrediction.setTimesUsed(categoryPrediction.getTimesUsed()+1);
            } else {
                categoryPrediction.setTimesRejected(categoryPrediction.getTimesRejected()+1);
                if (categoryPrediction.getTimesRejected() > categoryPrediction.getTimesUsed()){
                    categoryPrediction.setSubCategory(subCategory);
                    categoryPrediction.setTimesRejected(0);
                    categoryPrediction.setTimesUsed(1);
                    categoryPredictionRepository.save(categoryPrediction);
                }
            }

        }

        categoryPredictionRepository.save(categoryPrediction);
    }

    /**
     * Tries to sanitize the informed string, keeping just what is relevant to predict the
     * correct category.
     * @param description
     * @return
     */
    private String sanitize(String description){

        // Get all pattern that need to be applied.
        List<SanitizePattern> patterns = sanitizePatternRepository.findAll();
        for (SanitizePattern pattern: patterns) {
            description = description.replaceAll(pattern.getPattern(), pattern.getReplaceFor()).trim();
        }

//        // Elimina padrão de prestação da descrição da despesa (00/00)
//        description = description.replaceAll("\d{2}/\d{2}", "").trim();
//
//        // Elimina diferenciação de lançamento do Uber
//        if (description.startsWith("Uber UBER")){
//            description = "Uber UBER";
//        }

        return description;
    }

}
