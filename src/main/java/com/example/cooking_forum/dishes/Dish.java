package com.example.cooking_forum.dishes;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
@Document
public class Dish {
    @Id
    private String id;
    @Indexed()
    @NotNull
    @Size(min=3, message = "The recipe must contain a name!")
    private String name;
    @NotNull
    @Size(min=10, message = "You must add a description!")
    private String description;
    @NotNull
    @Size(min=2, message = "Input at least 2 ingredients!")
    private List<String> ingredients;
    @NotNull
    @Size(min=20, message = "You mush explain how to prepare the dish!")
    private String howToPrepare;
    private String imageSource;
    private String imagePublicId;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime publishedDateTime;
    @Indexed
    //the user who published the recipe
    private String ownerOfDishId;

    private List<Comment> dishComments;

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    public void setIngredients(List<String> ingredients) {
        if (ingredients != null) {
            this.ingredients = ingredients;
        }
    }

    public void setHowToPrepare(String howToPrepare) {
        if (howToPrepare != null) {
            this.howToPrepare = howToPrepare;
        }
    }

    public void setOwnerOfDishId(String id){
        if(id != null){
            this.ownerOfDishId = id;
        }
    }

    public void setImageSource(String imageSource){
        if(imageSource != null){
            this.imageSource = imageSource;
        }
    }
}
