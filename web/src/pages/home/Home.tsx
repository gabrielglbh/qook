import Card from "../../components/Card";

export default function Home () {
    const onClicklRecipeButton = () => {
        console.log("Recipe");
    };

    const onClickShoppingListButton = () => {
        console.log("Shopping List");
    };

    return (
        <div className="mx-auto h-screen content-center ms-4 me-4">
            <h1 className="text-5xl font-bold mb-8 text-center">
                Bienvenido a Qook
            </h1>
            <div className="grid grid-rows-2 grid-cols-1 place-items-center gap-4">
                <Card title={"Recetas"} onClick={onClicklRecipeButton}/>
                <Card title={"Lista de la compra"} onClick={onClickShoppingListButton}/>
            </div>
        </div>
    )
}
