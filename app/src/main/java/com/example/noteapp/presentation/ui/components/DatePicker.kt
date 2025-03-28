import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteapp.presentation.ui.components.CircularPicker
import kotlinx.coroutines.launch
import java.util.*



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomDatePicker(onDateSelected: (Int, Int, Int) -> Unit) {
    val today = Calendar.getInstance()
    val dateHandler = remember { DateHandler(today) }

    var selectedMonth by remember { mutableStateOf(dateHandler.currentMonth) }
    var selectedYear by remember { mutableIntStateOf(dateHandler.currentYear) }
    var selectedDay by remember { mutableIntStateOf(dateHandler.currentDay) }

    onDateSelected(selectedDay, getMonthNumber(selectedMonth), selectedYear)

    val days by remember(selectedMonth, selectedYear) {
        derivedStateOf { dateHandler.getDaysForMonth(selectedMonth, selectedYear) }
    }

    val months = dateHandler.getMonths()
    val years = dateHandler.getYearsRange()

    LaunchedEffect(selectedMonth, selectedYear) {
        if (selectedDay !in days) {
            selectedDay = days.lastOrNull() ?: 1
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        CircularPicker(days, selectedDay) { newDay ->
            selectedDay = newDay
        }
        CircularPicker(months, selectedMonth) { newMonth ->
            selectedMonth = newMonth
        }
        CircularPicker(years, selectedYear) { newYear ->
            selectedYear = newYear
        }
    }
}

class DateHandler(calendar: Calendar) {
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    val currentMonth = getMonths()[calendar.get(Calendar.MONTH)]
    val currentYear = calendar.get(Calendar.YEAR)

    fun getDaysForMonth(month: String, year: Int): List<Int> {
        val monthIndex = getMonths().indexOf(month)
        val daysInMonth = when (monthIndex) {
            1 -> if (isLeapYear(year)) 29 else 28
            3, 5, 8, 10 -> 30
            else -> 31
        }
        return (1..daysInMonth).toList()
    }

    fun getMonths() = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    fun getYearsRange() = (currentYear - 50..currentYear + 50).toList()

    private fun isLeapYear(year: Int) = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun getMonthNumber(monthName: String): Int {
    val monthFullNames = mapOf(
        "Jan" to "JANUARY", "Feb" to "FEBRUARY", "Mar" to "MARCH", "Apr" to "APRIL",
        "May" to "MAY", "Jun" to "JUNE", "Jul" to "JULY", "Aug" to "AUGUST",
        "Sep" to "SEPTEMBER", "Oct" to "OCTOBER", "Nov" to "NOVEMBER", "Dec" to "DECEMBER"
    )

    val fullMonthName = monthFullNames[monthName] ?: monthName.uppercase()

    return try {
        java.time.Month.valueOf(fullMonthName).value  // Convert full month name to number
    } catch (e: IllegalArgumentException) {
        -1  // Invalid month name
    }
}