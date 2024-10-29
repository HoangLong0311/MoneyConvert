import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextSource: EditText
    private lateinit var editTextTarget: EditText
    private lateinit var spinnerSourceCurrency: Spinner
    private lateinit var spinnerTargetCurrency: Spinner

    private var isSourceSelected = true // Để kiểm tra EditText nào là nguồn

    // Tỉ giá ví dụ
    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "JPY" to 110.0,
        "VND" to 23000.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextSource = findViewById(R.id.editTextSource)
        editTextTarget = findViewById(R.id.editTextTarget)
        spinnerSourceCurrency = findViewById(R.id.spinnerSourceCurrency)
        spinnerTargetCurrency = findViewById(R.id.spinnerTargetCurrency)

        // Cài đặt Adapter cho Spinner
        val currencies = exchangeRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSourceCurrency.adapter = adapter
        spinnerTargetCurrency.adapter = adapter

        // Theo dõi thay đổi dữ liệu trong EditText
        editTextSource.addTextChangedListener(currencyTextWatcher)
        editTextTarget.addTextChangedListener(currencyTextWatcher)

        // Xác định EditText nào là nguồn và đích
        editTextSource.setOnFocusChangeListener { _, hasFocus -> isSourceSelected = hasFocus }
        editTextTarget.setOnFocusChangeListener { _, hasFocus -> isSourceSelected = !hasFocus }

        // Cập nhật kết quả khi chọn loại tiền tệ khác
        spinnerSourceCurrency.onItemSelectedListener = currencySelectedListener
        spinnerTargetCurrency.onItemSelectedListener = currencySelectedListener
    }

    // TextWatcher để lắng nghe thay đổi trong EditText và cập nhật kết quả
    private val currencyTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            convertCurrency()
        }
    }

    // Listener để cập nhật khi thay đổi loại tiền tệ
    private val currencySelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            convertCurrency()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    // Hàm chuyển đổi tiền tệ
    private fun convertCurrency() {
        val sourceAmount = if (isSourceSelected) editTextSource.text.toString().toDoubleOrNull() ?: 0.0 else editTextTarget.text.toString().toDoubleOrNull() ?: 0.0
        val sourceCurrency = if (isSourceSelected) spinnerSourceCurrency.selectedItem.toString() else spinnerTargetCurrency.selectedItem.toString()
        val targetCurrency = if (isSourceSelected) spinnerTargetCurrency.selectedItem.toString() else spinnerSourceCurrency.selectedItem.toString()

        val sourceRate = exchangeRates[sourceCurrency] ?: 1.0
        val targetRate = exchangeRates[targetCurrency] ?: 1.0

        // Chuyển đổi theo tỉ giá
        val convertedAmount = sourceAmount * (targetRate / sourceRate)

        // Hiển thị kết quả
        if (isSourceSelected) {
            editTextTarget.setText(convertedAmount.toString())
        } else {
            editTextSource.setText(convertedAmount.toString())
        }
    }
}
