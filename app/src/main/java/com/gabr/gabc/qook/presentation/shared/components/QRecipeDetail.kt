package com.gabr.gabc.qook.presentation.shared.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.presentation.shared.QDateUtils.Companion.formatDate
import com.gabr.gabc.qook.presentation.theme.AppTheme
import java.util.Calendar

@Composable
fun QRecipeDetail(
    recipe: Recipe,
    modifier: Modifier,
    op: User? = null,
    shareRecipe: (() -> Unit)? = null,
    addToOwnRecipesButton: @Composable (() -> Unit)? = null,
    onRecipeUrlClick: (() -> Unit)? = null
) {
    val config = LocalConfiguration.current

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        if (recipe.photo != Uri.EMPTY) QImageContainer(
            uri = recipe.photo,
            placeholder = Icons.Outlined.Photo,
        )
        Spacer(modifier = Modifier.size(12.dp))
        if (shareRecipe != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                Text(
                    recipe.name,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp, start = 60.dp)
                )
                IconButton(
                    onClick = {
                        shareRecipe()
                    }
                ) {
                    Icon(Icons.Outlined.Share, "")
                }
            }
        } else {
            Text(
                recipe.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.size(8.dp))
        LazyRow(
            content = {
                items(recipe.tags) { tag ->
                    Box(
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        QTag(tag = tag)
                    }
                }
            }
        )
        if (op != null) Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            QImageContainer(uri = op.photo, placeholder = Icons.Outlined.Person, size = 48.dp)
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                stringResource(R.string.recipe_details_op, op.name),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        if (addToOwnRecipesButton != null) Spacer(modifier = Modifier.size(8.dp))
        if (addToOwnRecipesButton != null) addToOwnRecipesButton()
        Spacer(modifier = Modifier.size(8.dp))
        QContentCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(36.dp)
            ) {
                TextWithIcon(
                    icon = Icons.Outlined.Bolt,
                    text = when (recipe.easiness) {
                        Easiness.EASY -> stringResource(R.string.add_recipe_easiness_EASY)
                        Easiness.MEDIUM -> stringResource(R.string.add_recipe_easiness_MEDIUM)
                        Easiness.HARD -> stringResource(R.string.add_recipe_easiness_HARD)
                    },
                    modifier = Modifier.weight(1f)
                )
                TextWithIcon(
                    icon = Icons.Outlined.Timer,
                    text = recipe.time,
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 8.dp,
                    horizontal = config.screenWidthDp.dp / 4
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(36.dp)
            ) {
                TextWithIcon(
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    text = recipe.creationDate.formatDate(),
                    modifier = Modifier.weight(1f)
                )
                TextWithIcon(
                    icon = Icons.Outlined.ModeEdit,
                    text = recipe.updateDate.formatDate(),
                    modifier = Modifier.weight(1f)
                )
            }
            if (recipe.recipeUrl != null) HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = 8.dp,
                    horizontal = config.screenWidthDp.dp / 4
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (recipe.recipeUrl != null) Surface(
                onClick = {
                    onRecipeUrlClick?.let { it() }
                },
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent,
            ) {
                Text(
                    recipe.recipeUrl,
                    color = MaterialTheme.colorScheme.tertiary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                )
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        QContentCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundContent = {
                Icon(
                    Icons.AutoMirrored.Outlined.ReceiptLong,
                    contentDescription = null,
                    modifier = it
                )
            }
        ) {
            Text(
                stringResource(R.string.recipe_details_ingredients),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                recipe.ingredients.forEach {
                    QIngredient(ingredient = it)
                }
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        if (recipe.description.isNotEmpty()) QContentCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundContent = {
                Icon(
                    Icons.Outlined.ContentPaste,
                    contentDescription = null,
                    modifier = it
                )
            }
        ) {
            Text(
                stringResource(R.string.recipe_details_steps),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Column {
                recipe.description.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        QDescriptionStep(
                            step = step,
                            stepIndex = index,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun TextWithIcon(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Icon(
        icon,
        "",
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.size(4.dp))
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewQRecipeDetail() {
    AppTheme {
        QRecipeDetail(
            recipe = Recipe(
                "",
                "Berenjenas rellenas",
                Calendar.getInstance().time,
                Calendar.getInstance().time,
                Easiness.MEDIUM,
                "5 minutos",
                Uri.EMPTY,
                "url test",
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
            ), modifier = Modifier.padding(12.dp)
        )
    }
}
