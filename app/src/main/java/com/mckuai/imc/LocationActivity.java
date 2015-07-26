package com.mckuai.imc;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mckuai.bean.MCUser;
import com.umeng.analytics.MobclickAgent;

public class LocationActivity extends BaseActivity implements OnClickListener, OnItemClickListener
{
	private static final String TAG = "LocationActivity";
	private Button btn_showOwner;
	private Context mContext;
	private LayoutInflater inflater;
	private TextView display_addr;
	private BaseAdapter adapter;
	private ListView mCityLit;
	private LocationClient mLocationClient;
	private MyLocationListener myListener;
	private AsyncHttpClient client;
	private String[] cityname = new String[] { "鹤岗市", "佳木斯市", "七台河市", " 牡丹江市", "鸡西市", " 齐齐哈尔市", " 哈尔滨市", " 白山市",
			" 松原市", " 辽源市", " 白城市", "通化市", "四平市", "延边朝鲜族自治州", " 吉林市", " 长春市", "葫芦岛市", "盘锦市", "朝阳市", " 辽阳市", " 阜新市",
			" 营口市", "锦州市", " 丹东市", " 本溪市", " 抚顺市", " 鞍山市", " 大连市", " 铁岭市", "沈阳市", " 运城市", " 吕梁市", " 临汾市", " 晋城市",
			" 长治市", "晋中市", " 阳泉市", " 大同市", " 太原市", " 忻州市", " 朔州市", " 秦皇岛市", "邢台市", " 衡水市", " 沧州市", " 廊坊市", " 唐山市",
			"承德市 ", "张家口市", " 保定市", " 石家庄市", " 邯郸市", " 重庆市", " 上海市", " 天津市", " 北京市", " 新余市", " 鹰潭市", "南平市", " 三明市",
			" 龙岩市", " 漳州市", " 泉州市", " 莆田市", "宁德市", " 厦门市 ", "福州市 ", "宣城市", " 池州市", "亳州市", " 六安市", " 巢湖市 ", "宿州市",
			" 阜阳市", " 滁州市", " 黄山市 ", "安庆市", " 铜陵市", " 淮北市", " 马鞍山市", " 淮南市", " 蚌埠市", " 芜湖市", " 合肥市", " 台州市", " 金华市",
			" 丽水市", " 温州市 ", "绍兴市", " 宁波市 ", "嘉兴市 ", "湖州市 ", "杭州市 ", "衢州市 ", "舟山市 ", "宿迁市", " 泰州市 ", "常州市 ", "连云港市",
			" 淮安市 ", "徐州市 ", "盐城市 ", "扬州市", " 南通市 ", "苏州市 ", "镇江市", " 无锡市 ", "南京市 ", "大庆市 ", "伊春市 ", "大兴安岭地区 ", "黑河市",
			" 绥化市 ", "双鸭山市 ", "宜昌市 ", "荆州市 ", "咸宁市", " 黄石市 ", "黄冈市 ", "孝感市", " 鄂州市", " 襄阳市", " 武汉市", " 三门峡市", " 驻马店市 ",
			"漯河市 ", "周口市 ", "濮阳市", " 鹤壁市", " 焦作市", " 济源市", "洛阳市 ", "开封市 ", "南阳市 ", "信阳市 ", "平顶山市 ", "许昌市", " 新乡市 ",
			"安阳市", " 郑州市", " 商丘市 ", "聊城市", " 莱芜市 ", "日照市 ", "枣庄市", " 威海市 ", "东营市", " 滨州市 ", "临沂市 ", "泰安市 ", "济宁市 ",
			"潍坊市 ", "烟台市", " 德州市 ", "淄博市 ", "青岛市 ", "济南市 ", "菏泽市", " 萍乡市 ", "景德镇市", " 赣州市 ", "吉安市 ", "宜春市 ", "抚州市 ",
			"上饶市 ", "九江市", " 南昌市", "临夏回族自治州", " 陇南市 ", "定西市", " 庆阳市", " 酒泉市 ", "平凉市 ", "张掖市 ", "武威市 ", "嘉峪关市", " 天水市 ",
			"白银市 ", "金昌市 ", "兰州市 ", "东莞市", " 潮州市 ", "云浮市 ", "清远市", " 河源市 ", "中山市", " 湛江市", " 肇庆市", " 佛山市", " 珠海市 ",
			"深圳市", " 汕头市", " 梅州市 ", "韶关市 ", "江门市 ", "惠州市 ", "茂名市 ", "揭阳市 ", "阳江市", " 汕尾市 ", "广州市 ", "永州市 ", "怀化市",
			" 张家界市 ", "湘西土家族苗族自治州 ", "邵阳市", " 娄底市 ", "益阳市 ", "常德市", " 郴州市 ", "衡阳市 ", "株洲市 ", "湘潭市 ", "长沙市 ", "岳阳市",
			" 潜江市", " 天门市 ", "仙桃市 ", "荆门市 ", "随州市 ", "十堰市 ", "乐东黎族自治县", " 昌江黎族自治县", " 白沙黎族自治县 ", "临高县", " 屯昌县 ",
			"定安县 ", "澄迈县", " 东方市 ", "万宁市", " 文昌市 ", "儋州市 ", "琼海市", " 五指山市 ", "三亚市 ", "海口市 ", "黔西南布依族苗族自治州 ", "六盘水市",
			" 毕节地区 ", "铜仁地区", " 黔东南苗族侗族自治州 ", "黔南布依族苗族自治州 ", "安顺市 ", "遵义市", " 贵阳市 ", "广元市 ", "德阳市 ", "阿坝藏族羌族自治州 ",
			"甘孜藏族自治州 ", "雅安市", " 凉山彝族自治州 ", "眉山市", " 乐山市 ", "内江市", " 资阳市", " 宜宾市 ", "泸州市", " 巴中市", " 广安市 ", "遂宁市",
			" 达州市", " 南充市 ", "绵阳市 ", "自贡市", " 攀枝花市", " 成都市", " 甘南藏族自治州", "来宾市", "崇左市 ", "南宁市 ", "防城港市", "铜川市 ", "宝鸡市",
			" 汉中市 ", "安康市", " 商洛市", "渭南市 ", "榆林市 ", "延安市", " 咸阳市 ", "西安市 ", "海西蒙古族藏族自治州 ", "玉树藏族自治州 ", "果洛藏族自治州",
			" 海南藏族自治州 ", "黄南藏族自治州 ", "海东地区 ", "西宁市", " 海北藏族自治州", " 丽江市 ", "迪庆藏族自治州", " 怒江傈傈族自治州", " 临沧市", " 普洱市",
			" 楚雄彝族自治州 ", "玉溪市 ", "文山壮族苗族自治州", " 保山市", " 曲靖市", " 红河哈尼族彝族自治州 ", "大理白族自治州", " 昆明市", " 昭通市 ", "德宏傣族景颇族自治州",
			" 西双版纳傣族自治州 ", "琼中黎族苗族自治县", " 保亭黎族苗族自治县 ", "陵水黎族自治县  ", "通辽市 ", "乌兰察布市 ", "乌海市 ", "包头市", "呼和浩特市 ",
			"呼伦贝尔市 ", "伊犁哈萨克自治州 ", "图木舒克市 ", "喀什地区 ", "阿拉尔市 ", "阿克苏地区 ", "巴音郭楞蒙古自治州 ", "吐鲁番地区 ", "五家渠市 ", "昌吉回族自治州 ",
			"石河子市 ", "乌鲁木齐市", " 克拉玛依市 ", "博尔塔拉蒙古自治州", " 克孜勒苏柯尔克孜自治州 ", "阿勒泰地区 ", "和田地区 ", "哈密地区 ", "塔城地区 ", "中卫市 ",
			"固原市", " 吴忠市", " 石嘴山市", " 银川市", " 阿里地区", " 那曲地区 ", "昌都地区 ", "林芝地区 ", "山南地区", " 日喀则地区 ", "拉萨市 ", "北海市 ",
			"河池市 ", "钦州市 ", "百色市", " 玉林市 ", "贵港市 ", "贺州市 ", "梧州市", " 桂林市", " 柳州市" };

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		inflater = LayoutInflater.from(this);
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("定位页");

		btn_showOwner = (Button) findViewById(R.id.btn_showOwner);
		btn_showOwner.setVisibility(View.GONE);
		findViewById(R.id.btn_left).setOnClickListener(this);
		TextView title = (TextView) findViewById(R.id.tv_title);
		title.setText(getString(R.string.addrs));
		display_addr = (TextView) findViewById(R.id.display_addr);
		mCityLit = (ListView) findViewById(R.id.city_list);
		myListener = new MyLocationListener();
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.setLocOption(getOption());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.start();
		mLocationClient.requestLocation();

		mCityLit.setAdapter(new ListAdapter());
		mCityLit.setOnItemClickListener(this);
		client = MCkuai.getInstance().mClient;

	}
	
	/* (non-Javadoc)
	 * @see com.mckuai.imc.activity.BaseActivity#onPause()
	 */
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("定位页");
	}

	private void initView()
	{

	}

	public class ListAdapter extends BaseAdapter
	{

		public ListAdapter()
		{
			// TODO Auto-generated constructor stub
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return cityname.length;
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return cityname[position];
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			ViewHolder viewHolder;

			if (convertView == null)
			{

				convertView = inflater.inflate(R.layout.item_address, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.ct_name = (TextView) convertView.findViewById(R.id.ct_name);
				convertView.setTag(viewHolder);
			} else
			{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String city = (String) getItem(position);
			viewHolder.ct_name.setText(city);
			return convertView;
		}

	}

	class ViewHolder
	{
		public TextView ct_name;
	}

	public class MyLocationListener implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation location)
		{
			// TODO Auto-generated method stub

			if (null == location)
			{
				Log.e(TAG, "NULL");
				return;
			}
			StringBuffer sb = new StringBuffer(256);

			sb.append(location.getCity());
			location.getLocType();
			Log.e(TAG, "type:" + location.getLocType());
			display_addr.setVisibility(View.VISIBLE);
			display_addr.setText(sb.toString());
			mLocationClient.stop();
			MCkuai app = MCkuai.getInstance();
			app.getUser();
			if (app.getUser() != null && app.getUser().getId() != 0)
			{
				MCUser user = app.getUser();
				user.setAddr(location.getCity());
				app.setUser(user);
			}

		}

	}

	private LocationClientOption getOption()
	{
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(false);// 返回的定位结果包含手机机头的方向
		return option;
	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{

		case R.id.btn_left:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onStop()
	{
		// TODO Auto-generated method stub
		super.onStop();
		if (mLocationClient == null)
		{
			try
			{
				mLocationClient.stop();
			} catch (Exception e)
			{
				// TODO: handle exception
			}
			mLocationClient.unRegisterLocationListener(myListener);
		}
	}

	protected void updataAddr(String addr)
	{
		final RequestParams params = new RequestParams();
		if (null != MCkuai.getInstance().getUser())
		{
			params.put("id", MCkuai.getInstance().getUser().getId());
			params.put("addr", addr);
		}
		final String url = getString(R.string.interface_domainName) + getString(R.string.interface_updateLocation);
		//Log.e(TAG, url + "&" + params.toString());
		client.get(url, params, new JsonHttpResponseHandler()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.AsyncHttpResponseHandler#onStart()
			 */
			@Override
			public void onStart()
			{
				// TODO Auto-generated method stub
				popupLoadingToast("正在更新位置");
				super.onStart();

			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onSuccess
			 * (int, org.apache.http.Header[], org.json.JSONObject)
			 */
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response)
			{
				// TODO Auto-generated method stub
				if (null != response && response.has("state"))
				{
					try
					{
						if (response.getString("state").equalsIgnoreCase("ok"))
						{
							cancleLodingToast(true);
							MCkuai.getInstance().getUser().setAddr(display_addr.getText().toString());
							return;
						}
					} catch (Exception e)
					{
						// TODO: handle exception
						Toast.makeText(LocationActivity.this, "更新地址失败，原因："+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
						cancleLodingToast(false);
						return;
					}
				}
				Toast.makeText(LocationActivity.this, "更新地址失败，请稍候再试！", Toast.LENGTH_SHORT).show();;
				cancleLodingToast(false);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.loopj.android.http.JsonHttpResponseHandler#onFailure
			 * (int, org.apache.http.Header[], java.lang.Throwable,
			 * org.json.JSONObject)
			 */
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse)
			{
				// TODO Auto-generated method stub
				cancleLodingToast(false);
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		display_addr.setVisibility(View.VISIBLE);
		display_addr.setText(cityname[position]);
		updataAddr(cityname[position]);
	}

}
