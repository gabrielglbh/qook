import { useEffect, useState } from "react";
import Button from "../../components/Card";
import Title from "../../components/Title";
import { getRecipes } from "../../services/database/RecipeService";
import Recipe from "../../models/recipe/Recipe";

export default function Home () {
    const [recipes, updateRecipes] = useState<Recipe[]>([])

    const onClicklRecipeButton = () => {
        console.log("Recipe");
    };

    const onClickShoppingListButton = () => {
        console.log("Shopping List");
    };

    const RecipeList = () => {
        return(
            <ul>
                {recipes.map(recipe => 
                    <li key={recipe.id}>{recipes.length}</li>
                )}
            </ul>
        );
    }

    useEffect(() => {
        const loadRecipes = async () => {
            const remoteRecipes = await getRecipes(null, null, "")
            console.log("Console.log")
            updateRecipes(remoteRecipes)
        };  
        console.log(recipes)
        loadRecipes()
    }, [])

    return (
        <div className="mx-auto h-screen content-center bg-orange-300 grid grid-cols-2">
            <div className="container lg:bg-white p-12 lg:shadow-md lg:rounded lg:container lg:max-w-md">
                <Title title="Bienvenido a Qook"/>
                <div className="grid grid-rows-2 grid-cols-1 place-items-center gap-4">
                    <Button title={"Recetas"} onClick={onClicklRecipeButton}/>
                    <Button title={"Lista de la compra"} onClick={onClickShoppingListButton}/>
                </div>
            </div>
            <div className="container lg:bg-white p-12 lg:shadow-md lg:rounded lg:container lg:max-w-md">
                {recipes.length == 0 ? "No hay recetas" : RecipeList()}
            </div>
        </div>
    )
}
