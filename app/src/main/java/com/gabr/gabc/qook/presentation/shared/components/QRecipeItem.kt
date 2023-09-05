package com.gabr.gabc.qook.presentation.shared.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.theme.AppTheme
import java.util.Calendar

@Composable
fun QRecipeItem(
    recipe: Recipe,
    modifier: Modifier = Modifier,
    simplified: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Surface(
        enabled = onClick != null,
        onClick = { onClick?.let { it() } },
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = modifier.fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextWithIcon(
                        icon = Icons.Outlined.Bolt,
                        text = when (recipe.easiness) {
                            Easiness.EASY -> stringResource(R.string.add_recipe_easiness_EASY)
                            Easiness.MEDIUM -> stringResource(R.string.add_recipe_easiness_MEDIUM)
                            Easiness.HARD -> stringResource(R.string.add_recipe_easiness_HARD)
                        },
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("•")
                    Spacer(modifier = Modifier.size(8.dp))
                    TextWithIcon(
                        icon = Icons.Outlined.Timer,
                        text = recipe.time,
                    )
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        items(recipe.tags) { tag ->
                            QTag(
                                tag = tag,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                )
            }
            if (recipe.photo != Uri.EMPTY) QImageContainer(
                uri = recipe.photo,
                placeholder = Icons.Outlined.Photo,
                shape = MaterialTheme.shapes.large,
                size = if (simplified) {
                    72.dp
                } else {
                    100.dp
                }
            )
        }
    }
}

@Composable
private fun TextWithIcon(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            "",
            tint = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = modifier,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQRecipeItem() {
    AppTheme {
        QRecipeItem(
            recipe = Recipe(
                "",
                "Berenjenas rellenas",
                Calendar.getInstance().time,
                Calendar.getInstance().time,
                Easiness.MEDIUM,
                "5 minutos",
                Uri.EMPTY,
                "test url",
                listOf(
                    "Cortamos la parte del tallo de la berenjena. Después la cortamos a lo largo, por la mitad y a cada mitad, le vamos a hacer unos cortes con un cuchillo. Sin llegar a la parte de la piel. Le hacemos unos cortes, primero a lo largo y luego a lo ancho, para obtener al final unos cortes «en forma de rejilla». Hecho esto, las ponemos en una bandeja de horno, regamos la parte de la carne con un pequeño chorrito de aceite (la berenjena lo absorberá enseguida) y lo metemos en el horno, previamente calentado a 180ºC. Dejamos aquí unos 20 minutos",
                    "Pasado el tiempo, retiramos y esperamos que se templen un poco. Después, con una cuchara retiramos la pulpa, manteniendo la cáscara intacta. Al estar parcialmente horneada, se separará muy fácil",
                    "En una sartén grande, ponemos a calentar un chorrito de aceite de oliva a fuego suave. Una vez esté caliente, añadimos los dientes de ajo, la cebolla y el pimiento rojo bien picados. Salpimentamos y dejamos cocinar unos 15 minutos, mientras removemos con frecuencia",
                    "Pasados los 15 minutos, añadimos la carne de la berenjena, previamente picada. Incorporamos con el resto de la verdura y añadimos la carne picada de cerdo y de ternera. Con la ayuda de una cuchara de madera, vamos desmenuzando la carne, para que no queden mazacotes muy grandes y para ir integrándola con el resto de ingredientes. Cocinamos dos o tres minutos, hasta que pierda el color a crudo",
                    "Ahora añadimos el tomate triturado y las cucharaditas de tomillo y romero. Personalmente la combinación de estas dos especias me encanta pero, puedes sustituirlas por una cucharada de orégano. Mezclamos e integramos todos los ingredientes y después, dejamos cocer todo a fuego suave unos 30 minutos. Hasta que se evaporen todo el agua que ha soltado la carne picada y el tomate",
                    "Mientras se evapora, preparamos la salsa bechamel. En otra olla alta, ponemos a calentar la mantequilla (puedes sustituirla por aceite de oliva) y una vez se haya derretido, añadimos la harina.  Mezclamos y tostamos unos 3 minutos. Esta mezcla, recibe el nombre de «roux». Pasado el tiempo, vamos añadiendo poco a poco la leche. Añadimos unos 200ml y mezclamos, hasta que se integre con la roux. Hecho esto, volvemos a añadir otros 200ml y volvemos a integrar. Repetimos este paso hasta acabar con toda la leche -y lograr una salsa bechamel ligera-. Es entonces cuando añadimos una cucharadita de nuez moscada y reservamos",
                    "Con todo esto hecho, tan sólo queda rellenar las berenjenas. Cogemos las cáscaras y las rellenamos con generosa cantidad del relleno que habíamos preparado. Puedes añadir a cada cáscara la cantidad de relleno que quieras. Lo importante es acabar con todo. Hecho esto, regamos cada berenjena rellena con salsa bechamel. Finalmente espolvoreamos con queso y rallado y las introducimos de nuevo en el horno, función grill a 200ºC. Las dejamos aquí unos 10 minutos, hasta que el queso se gratine."
                ),
                listOf(
                    "4 berenjenas",
                    "1 cebolla",
                    "2 dientes de ajo",
                    "1 pimiento rojo",
                    "300g de carne picada de cerdo",
                    "300g de carne picada de ternera",
                    "500g de tomate triturado",
                    "1 cucharadita de tomillo seco",
                    "1 cucharadita de romero seco",
                    "70g de harina",
                    "70g de mantequilla",
                    "1l de leche entera",
                    "1 cucharadita de nuez moscada",
                    "100g de queso rallado",
                    "sal y pimienta",
                    "aceite de oliva"
                ),
                listOf(
                    Tag("", "Sano", Color.Blue),
                    Tag("", "Rápido", Color.Red),
                    Tag("", "Vegetal", Color.Green)
                )
            ), modifier = Modifier.padding(4.dp), simplified = true
        )
    }
}