package com.finddreams.sortedcontact;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finddreams.sortedcontact.sortlist.CharacterParser;
import com.finddreams.sortedcontact.sortlist.SideBar;
import com.finddreams.sortedcontact.sortlist.SideBar.OnTouchingLetterChangedListener;
import com.finddreams.sortedcontact.sortlist.SortModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


/**
 * @Description:鑱旂郴浜烘樉绀虹晫闈�
 * @author http://blog.csdn.net/finddreams
 */
public class MainActivity extends Activity {
	
	
	
	
	
	
	
	
		private TextView status;
	    private EditText txtContent;
	    private Button btnSynthesize;
	    private String textToSynthesize;
	    private Synthesizer m_syn;

	
	
	
	
	
	
	
	
	private View mBaseView;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private Map<String, String> callRecords;

	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	private PinyinComparator pinyinComparator;
	String name,number;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_contact);
		
		
		
		
		
		
		status = (TextView)findViewById(R.id.status);
        status.setMovementMethod(new ScrollingMovementMethod());
       // txtContent = (EditText)findViewById(R.id.txtContent);
        //txtContent.setMovementMethod(new ScrollingMovementMethod());
        btnSynthesize = (Button)findViewById(R.id.btnSynthesize);

        // Note: The way to get api key:
        // Free: https://www.microsoft.com/cognitive-services/en-us/subscriptions?productId=/products/Bing.Speech.Preview
        // Paid: https://portal.azure.com/#create/Microsoft.CognitiveServices/apitype/Bing.Speech/pricingtier/S0
        m_syn = new Synthesizer("Your api key");
        Voice v = new Voice("en-US", "Microsoft Server Speech Text to Speech Voice (en-US, ZiraRUS)", Voice.Gender.Female, true);
        m_syn.SetVoice(v, null);
        btnSynthesize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	textToSynthesize ="Hello";
               // textToSynthesize = txtContent.getText().toString().trim();
                if(textToSynthesize == null || textToSynthesize.isEmpty()){
                    status.setText("The text to synthesize is empty!");
                }else{
                    status.setText("Synthesize...");
                    m_syn.SpeakToAudio(textToSynthesize);
                }
            }
        });
		
		
		
		
		
		
		
		initView();
		initData();
	}

	
	

	
	
	
	
	private void initView() {
		sideBar = (SideBar) this.findViewById(R.id.sidrbar);
		dialog = (TextView) this.findViewById(R.id.dialog);

		sortListView = (ListView) this.findViewById(R.id.sortlist);

	}

	private void initData() {
		// 瀹炰緥鍖栨眽瀛楄浆鎷奸煶绫�
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar.setTextView(dialog);

		// 璁剧疆鍙充晶瑙︽懜鐩戝惉
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@SuppressLint("NewApi")
			@Override
			public void onTouchingLetterChanged(String s) {
				// 璇ュ瓧姣嶉娆″嚭鐜扮殑浣嶇疆
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}
			}
		});

		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 杩欓噷瑕佸埄鐢╝dapter.getItem(position)鏉ヨ幏鍙栧綋鍓峱osition鎵�瀵瑰簲鐨勫璞�
				// Toast.makeText(getApplication(),
				// ((SortModel)adapter.getItem(position)).getName(),
				// Toast.LENGTH_SHORT).show();
			
				name= (((SortModel) adapter
							.getItem(position)).getName());
		
				 number = callRecords.get(((SortModel) adapter
						.getItem(position)).getName());
				Toast.makeText(MainActivity.this, name+number, 0).show();
				generate(view);
			}
		});

		new ConstactAsyncTask().execute(0);

	}

	
	public void generate(View view) {  //二维码生成
		Bitmap qrBitmap = generateBitmap(name+" "+number,400, 400);  
	    Toast.makeText(getApplicationContext(), "二维码生成成功", Toast.LENGTH_SHORT).show();
	    ImageView iv = (ImageView) findViewById(R.id.iv);
		iv.setImageBitmap(qrBitmap);  
	  
	}  
	
	
	private Bitmap generateBitmap(String content,int width, int height) {  //二维码生成
	    QRCodeWriter qrCodeWriter = new QRCodeWriter();  
	    Map<EncodeHintType, String> hints = new HashMap<EncodeHintType, String>();  
	    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
	    try {  
	        BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);  
	        int[] pixels = new int[width * height];  
	        for (int i = 0; i < height; i++) {  
	            for (int j = 0; j < width; j++) {  
	                if (encode.get(j, i)) {  
	                    pixels[i * width + j] = 0x00000000;  
	                } else {  
	                    pixels[i * width + j] = 0xffffffff;  
	                }  
	            }  
	        }  
	        return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);  
	    } catch (WriterException e) {  
	        e.printStackTrace();  
	    }  
	    return null;  
	}  
	
	
	
	
	private class ConstactAsyncTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... arg0) {
			int result = -1;
			callRecords = ConstactUtil.getAllCallRecords(MainActivity.this);
			result = 1;
			return result;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 1) {
				List<String> constact = new ArrayList<String>();
				for (Iterator<String> keys = callRecords.keySet().iterator(); keys
						.hasNext();) {
					String key = keys.next();
					constact.add(key);
				}
				String[] names = new String[] {};
				names = constact.toArray(names);
				SourceDateList = filledData(names);

				// 鏍规嵁a-z杩涜鎺掑簭婧愭暟鎹�
				Collections.sort(SourceDateList, pinyinComparator);
				adapter = new SortAdapter(MainActivity.this, SourceDateList);
				sortListView.setAdapter(adapter);

				mClearEditText = (ClearEditText) MainActivity.this
						.findViewById(R.id.filter_edit);
				mClearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						mClearEditText.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
						
					}
				});
				// 鏍规嵁杈撳叆妗嗚緭鍏ュ�肩殑鏀瑰彉鏉ヨ繃婊ゆ悳绱�
				mClearEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// 褰撹緭鍏ユ閲岄潰鐨勫�间负绌猴紝鏇存柊涓哄師鏉ョ殑鍒楄〃锛屽惁鍒欎负杩囨护鏁版嵁鍒楄〃
						filterData(s.toString());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	/**
	 * 涓篖istView濉厖鏁版嵁
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			// 姹夊瓧杞崲鎴愭嫾闊�
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 姝ｅ垯琛ㄨ揪寮忥紝鍒ゆ柇棣栧瓧姣嶆槸鍚︽槸鑻辨枃瀛楁瘝
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 鏍规嵁杈撳叆妗嗕腑鐨勫�兼潵杩囨护鏁版嵁骞舵洿鏂癓istView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 鏍规嵁a-z杩涜鎺掑簭
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

}
