import Card from "../../components/Card";
import Title from "../../components/Title";

export default function Home () {
    const onClicklRecipeButton = () => {
        console.log("Recipe");
    };

    const onClickShoppingListButton = () => {
        console.log("Shopping List");
    };

    return (
        <div className="mx-auto h-screen content-center bg-orange-300">
            <div className="container lg:bg-white p-12 lg:shadow-md lg:rounded lg:container lg:max-w-md">
                <Title title="Bienvenido a Qook"/>
                <div className="grid grid-rows-2 grid-cols-1 place-items-center gap-4">
                <Card title={"Recetas"} onClick={onClicklRecipeButton}/>
                <Card title={"Lista de la compra"} onClick={onClickShoppingListButton}/>
            </div>
            </div>
        </div>
    )
}
