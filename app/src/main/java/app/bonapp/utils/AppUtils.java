package app.bonapp.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bonapp.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import app.bonapp.BonAppApplication;
import app.bonapp.activities.HomeActivity;
import app.bonapp.constants.Constants;
import app.bonapp.customviews.TransparentProgressDialog;
import app.bonapp.interfaces.DialogButtonClickListener;


/**
 * Created by on 06-09-2016.
 */
public class AppUtils {
    private static AppUtils appUtils;
    private ProgressDialog mDialog;
    private String checkboxLactoVegText = "";
    private Calendar mCalendar;
    private Toast toast;
    private TransparentProgressDialog progressDialog;
    private Tracker mTracker;


    public static AppUtils getInstance() {
        if (appUtils == null) {
            appUtils = new AppUtils();
        }
        return appUtils;
    }

    public void setErrorTextMsg(TextView textView, String message) {
        textView.setVisibility(View.VISIBLE);
        textView.setText(message);
    }

    /**
     * Validate blank strings
     *
     * @param text
     * @return
     */
    public boolean validate(String text) {
        if (text != null && text.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

   /* public void setStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorLoginButton));
        }
    }

    public void setMyProfileStatusBar(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colormyprofleStatusbar));
        }
    }*/

    public void viewPagerChangeListener(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    public void textChange(EditText editText, final TextView textView) {

        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    textView.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }


    public void textChangeUpdate(EditText editText, final TextView textView) {

        editText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    textView.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * check Email
     *
     * @param target
     * @return
     */
    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * check Mobile Number
     *
     * @param target
     * @return
     */
    public boolean isValidNumber(CharSequence target) {
        if (target == null || target.length() < 7) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * show snackbar
     *
     * @param
     * @param
     */
    public void showSnackBar(View view, String message) {
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction("Action", null);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ffffff"));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#045461"));
        snackbar.show();
    }


    public void changeFocusToNext(final EditText text1, final EditText text2) {
        text1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (text1.getText().toString().length() == 1) {
                    text2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void afterTextChanged(Editable s) {
            }

        });
    }

    public void changeFocusToPrevious(final EditText text1, final EditText text2) {
        text1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (text1.getText().toString().length() == 0) {
                    text2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void afterTextChanged(Editable s) {
            }

        });
    }


    boolean res = false;


    public void showToast(Context context, String message) {

        /*//View layout = inflater.inflate(R.layout.layout_toast,(ViewGroup) findViewById(R.id.custom_toast_container));
        View layout=LayoutInflater.from(context).inflate(R.layout.layout_toast,null,false);
        TextView text = (TextView) layout.findViewById(R.id.tv_toast);
        text.setText(message);

        Toast toast = new Toast(context.getApplicationContext());
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
*/
        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();

    }



    public void hideNativeKeyboard(Activity pActivity) {
        if (pActivity != null) {
            if (pActivity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) pActivity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(pActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }



    public void showProgressDialog(Context context,boolean isCancelable) {
        /*mDialog = new ProgressDialog(context, R.style.MyTheme);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(isCancelable);
        mDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mDialog.setIndeterminateDrawable(context.getResources()
                .getDrawable(R.drawable.progress_bar_value));
        mDialog.show();*/
        progressDialog=new TransparentProgressDialog(context,R.drawable.app_progress);
        progressDialog.setCancelable(isCancelable);
        progressDialog.show();

    }

    /**
     * hide progress dialog
     */
    public void hideProgressDialog(Activity activity) {
       /* if (mDialog != null && !activity.isFinishing() )
            mDialog.dismiss();*/
       if (progressDialog!=null && !activity.isFinishing()){
           progressDialog.dismiss();
       }
    }


    public boolean isSdCardAvailable() {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (isSDPresent) {
            return true;
        } else
            return false;
    }

    public String getDataFromWeb(String baseUrl) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(baseUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(100000);
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
//read server response
            while ((line = bufferedReader.readLine()) != null) {
//append server response in string
                stringBuilder.append(line);
            }
            line = stringBuilder.toString();
            bufferedReader.close();
            httpURLConnection.disconnect();
            return line;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            httpURLConnection.disconnect();
        }
    }

    public String formateDate(String date_s) {
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        form.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = form.parse(date_s);
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            form.setTimeZone(tz);
            SimpleDateFormat postFormater = new SimpleDateFormat("MM-dd-yyyy, h:mm a");
            postFormater.setTimeZone(tz);
            //String current=postFormater.format(date);
            //String currentDate[]=current.split("-");
/*
            myear=Integer.parseInt(currentDate[0]);
            mMonth=Integer.parseInt(currentDate[1])-1;
            mday=Integer.parseInt(currentDate[2]);
*/
            return postFormater.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public String formateDateSGT(String date_s) {
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        form.setTimeZone(TimeZone.getTimeZone("SGT"));
        Date date = null;
        try {
            date = form.parse(date_s);
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            form.setTimeZone(tz);
            SimpleDateFormat postFormater = new SimpleDateFormat("MM-dd-yyyy, h:mm a");
            postFormater.setTimeZone(tz);
            //String current=postFormater.format(date);
            //String currentDate[]=current.split("-");
/*
            myear=Integer.parseInt(currentDate[0]);
            mMonth=Integer.parseInt(currentDate[1])-1;
            mday=Integer.parseInt(currentDate[2]);
*/
            return postFormater.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }


    public String getEncyptCardNo(String cardNo) {
        String card = "";
        String decrypted_card_num = cardNo;
        int space = (decrypted_card_num.trim().length() / 4) - 1;
        int star = ((decrypted_card_num.trim().length() / 4) - 1) * 4;
        for (int i = 0; i < space; i++) {
            card = card + "****" + " ";
        }
        card = card + decrypted_card_num.substring(star, decrypted_card_num.length());
        return card;

    }




    public void hideKeyTool(Activity activity, EditText editText) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void showKeyTool(Activity activity, EditText editText) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * this method check that is internet connection available or not
     *
     * @return boolean
     */
    public boolean isInternetOn(Activity pActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) pActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public Uri getPhotoUri() {
        Uri photoFileUri = null;
        String fileName = getFilename();
        File photoFile = new File(fileName);
        photoFileUri = Uri.fromFile(photoFile);
        return photoFileUri;
    }

    private String getFilename() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), Constants.APP_IMAGE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        //    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public String getFilePath() {
        File file = new File(Constants.APP_IMAGE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public Bitmap decodeFile(String path, int targetW, int targetH) {//you can provide file path here
        int orientation;
        try {
            if (path == null) {
                return null;
            }
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 0;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            Bitmap bm = BitmapFactory.decodeFile(path, bmOptions);
            Bitmap bitmap = bm;
            ExifInterface exif = new ExifInterface(path);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Log.e("ExifInteface .........", "rotation =" + orientation);
            Log.e("orientation", "" + orientation);
            Matrix m = new Matrix();
            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPathFromURI(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]
                        {
                                split[1]
                        };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    static public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    static public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    static public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    static public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * method to parse date
     *
     * @param commentDate
     * @return
     *//*
    public String parseDateToTime(String commentDate, Context context) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = simpleDateFormat.parse(String.valueOf(commentDate));
            Date todayDate = new Date();
            long diff = todayDate.getTime() - date.getTime();
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            int hours = (int) (diff / (1000 * 60 * 60));
            int minutes = (int) (diff / (1000 * 60));
            int seconds = (int) (diff / (1000));
            if (seconds < 60) {
                return context.getString(R.string.txt_just_now);
            } else if (minutes < 60) {
                return minutes + " " + context.getString(R.string.txt_mins_ago);
            } else if (hours < 24) {
                return hours + " " + context.getString(R.string.txt_hours_ago);
            } else {
                return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    /**
     * set views count
     *
     * @param count
     * @return
     */
    public String setCount(String count) {
        double viewCount = 0;
        try {
            viewCount = Double.parseDouble(count);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
        if (viewCount >= 100000) {
            return viewCount / 100000 + "L";
        } else if (viewCount > 1000) {
            return viewCount / 1000 + "K";
        } else
            return String.format(Locale.getDefault(), "%.0f", viewCount);

    }

    /**
     * method to share app
     */
    public void shareLink(String link, Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, link);

        try {
            context.startActivity(Intent.createChooser(intent, "Compartilhe a receita"));
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(context, context.getString(R.string.no_app_available), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * get current time stamp
     *
     * @return
     */
    public String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }



    public Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    /**
     * method to set date picker
     *
     * @param context
     * @param textView is the
     */
    public void setDate(Context context, final TextView textView, final TextView textView1) {
        mCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /*sDay = dayOfMonth;
                sMonth = monthOfYear;
                sYear = year;*/
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                textView.setText(getFormattedDate(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1) + "/" + String.valueOf(year)));
                if (textView1 != null) {
                    mCalendar.add(Calendar.DATE, 6);
                    textView1.setText(getFormattedDate(String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(mCalendar.get(Calendar.MONTH) + 1) + "/" + String.valueOf(mCalendar.get(Calendar.YEAR))));
                }
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        mCalendar.add(Calendar.DATE, 0);
        datePickerDialog.show();
    }

    private String getFormattedDate(String OurDate) {

        SimpleDateFormat formatter;
        try {
            formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date value = formatter.parse(OurDate);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy"); //this format changeable
            OurDate = dateFormatter.format(value);
        } catch (Exception e) {
            OurDate = "0000-00-00 00:00";
        }
        return OurDate;
    }


    public void clearAllNotifications(Context context) {
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    public void showAllowPermissionFromSettingDialog(final Activity mActivity) {
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.allow_permisisions);
        builder.setMessage(R.string.pls_enable_permisions);
        builder.setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", mActivity.getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
                dialogInterface.cancel();
            }
        });

    }

   /* *//**
     * this method converts hashmap to json and encrypt it and
     * put back it to  hashmap
     *
     * @param params
     * @return
     *//*
    public HashMap<String, String> encryptData(HashMap<String, String> params) {
        String json = new Gson().toJson(params);
        HashMap<String, String> param = new HashMap<>();
        try {
            //byte[] encryptReq = new MCrypt().encrypt(json);
            //String encp = new String(encryptReq, "UTF-8");
            //String enc=getEncrypted(json);
            String enc = encryptMsg(json, appUtils.generateKey(Constants.SecretKey));
            param.put("packet", enc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }

    *//**
     * this method converts hashmap to json and encrypt it and
     * put back it to  hashmap
     *
     * @param
     * @return
     *//*
    public HashMap<String, RequestBody> encryptMultiPartData(HashMap<String, String> params) {
        String json = new Gson().toJson(params);
        HashMap<String, RequestBody> param = new HashMap<>();
        try {
            //byte[] encryptReq = new MCrypt().encrypt(json);
            //String encp = new String(encryptReq, "UTF-8");
            //String newJson = json.replaceAll("\\\\", "");
            String enc = encryptMsg(json, appUtils.generateKey(Constants.SecretKey));
            param.put("packet", RestApi.getRequestBody(enc));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }





    *//*  *  * method to generate key to encrypt/decrypt server response  *  * *//*
    public javax.crypto.SecretKey generateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(key.getBytes(), "AES");
    }


    *//*
      *  *
      *  * method to encrypt server request
      *
      *  * *//*
    public String encryptMsg(String message, javax.crypto.SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        if (secretKey != null) {
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte iv[] = Constants.initVector.getBytes();
            //  new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
            return Base64.encodeToString(cipherText, Base64.NO_WRAP);
        }
        return "";
    }


    *//*
     *
     *  method to decrypt encrypted server response
     **//*
    public String decryptMsg(byte[] cipherText, javax.crypto.SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        if (secretKey != null) {
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte iv[] = Constants.initVector.getBytes();
            //  new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] ciphertextBytes = Base64.decode(cipherText, Base64.NO_WRAP);
            // String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
            byte[] original = cipher.doFinal(ciphertextBytes);
            String decryptString = new String(original, "UTF-8");

            return decryptString;
        }
        return "";
    }*/
    public boolean isLocationOnOrOff(Context mContext) {

        boolean gps_enabled = false;
        boolean network_enabled = false;
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            return false;

        } else {
            return true;
        }
    }

    /**
     *method to show alert dialog in app
     * This dialog appears everywhere in the app , either with title or not, either with two btns or one
     */
    public void showAlertDialog(Context context, String title, String message, String positiveBtn, String negativeBtn,
                                 final DialogButtonClickListener dialogButtonClickListener){
        final Dialog dialog=new Dialog(context,R.style.DialogAnimationZooma);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_alert_dialog);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.DIM_AMOUNT_CHANGED);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.9f;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView tvTitle=(TextView)dialog.findViewById(R.id.tv_title);
        tvTitle.setVisibility(title.equals("")?View.GONE:View.VISIBLE);
        tvTitle.setText(title);
        ((TextView)dialog.findViewById(R.id.tv_message)).setText(message);
        TextView tvPositiveBtn=(TextView)dialog.findViewById(R.id.tv_positive_btn);
        TextView tvNegativeBtn=(TextView)dialog.findViewById(R.id.tv_negative_btn);
        tvPositiveBtn.setText(positiveBtn);
        tvNegativeBtn.setVisibility(negativeBtn.equals("")?View.GONE:View.VISIBLE);
        tvNegativeBtn.setText(negativeBtn);
        tvPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogButtonClickListener!=null)
                dialogButtonClickListener.positiveButtonClick();
                dialog.dismiss();
            }
        });
        tvNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogButtonClickListener!=null)
                dialogButtonClickListener.negativeButtonClick();
                dialog.dismiss();
            }
        });
        //dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimationZoom;
        dialog.show();

    }

    public void highlightIcons(final EditText editText, final int deSelectedIcon, final int selectedIcon){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().length()==0){
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(deSelectedIcon,0,0,0);
                }else {
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(selectedIcon,0,0,0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void logoutFromApp(Context context,String message){
        if (message.length()!=0)
        appUtils.showToast(context,message);
        AppSharedPrefs.getInstance(context).clearPrefs(context);
        AppSharedPrefs.getInstance(context).putBoolean(AppSharedPrefs.PREF_KEY.IS_FIRST_TIME,false);
        Intent intent=new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * method to parse date
     *
     * @param endTime
     * @return
     */
    public String parseDateToTime(String endTime) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = simpleDateFormat.parse(String.valueOf(endTime));
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(gmttoLocalDate(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * method to parse date
     *
     * @param endTime
     * @return
     */
    public String parseDateToTimeAmPm(String endTime) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = simpleDateFormat.parse(String.valueOf(endTime));
            return new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(gmttoLocalDate(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * method to parse date
     *
     * @param endTime
     * @return
     */
    public String parseToTimeAndDate(String endTime) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = simpleDateFormat.parse(String.valueOf(endTime));
            return new SimpleDateFormat("HH:mm dd,MMM", Locale.getDefault()).format(gmttoLocalDate(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * method to parse date
     *
     * @param endTime
     * @return
     */
    public String parseToDateFormat(String endTime) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = simpleDateFormat.parse(String.valueOf(endTime));

            return new SimpleDateFormat("dd,MMM", Locale.getDefault()).format(AppUtils.getInstance().gmttoLocalDate(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date gmttoLocalDate(Date date) {

        String timeZone = Calendar.getInstance().getTimeZone().getID();
        Date local = new Date(date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime()));
        return local;
    }


    public void sendEmail(Context context,String mailTo,String subject,String body){
        /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",mailTo, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
*/
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {mailTo});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        //  emailIntent.putExtra(Intent.EXTRA_TEXT,html_css);
        /*String emailBody=String.format(mActivty.getResources().getString(R.string.invite_body_mail),
                AppSharedPreference.getsharedprefInstance(mActivty).getInviteUrl());*/
        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") ||
                    info.activityInfo.name.toLowerCase().contains("gmail")) best = info;

        try {
            if (best != null)
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            appUtils.showToast(context, context.getString(R.string.no_email_clients));
        }

    }


    public boolean isBeforeCurrentTime(String time) {
        Calendar calendar=Calendar.getInstance();
        int currentYear=calendar.get(Calendar.YEAR);
        String pattern = "HH:mm dd,MMM,yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time+","+currentYear);
            Date date2 = new Date();

            if (date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isYesterday(long date) {
        Calendar now = Calendar.getInstance();
        Calendar cdate = Calendar.getInstance();
        cdate.setTimeInMillis(date);

        now.add(Calendar.DATE,-1);

        return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
                && now.get(Calendar.DATE) == cdate.get(Calendar.DATE);
    }

    public boolean isToday(long date) {
        return DateUtils.isToday(date);
    }

    public long milliseconds(String date)
    {
        //String date_ = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = AppUtils.getInstance().gmttoLocalDate(mDate).getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
            return timeInMilliseconds;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param time1
     * @param time2
     * @return true if time is same
     */
    public boolean matchSameTime(String time1, String time2){
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time1);
            Date date2 = sdf.parse(time2);

            if(date1.compareTo(date2)==0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * this method converts hashmap to json and encrypt it and
     * put back it to  hashmap
     *
     * @param params
     * @return Encryption is removed so returning same parameters here.Because this method is used
    // at several places
     */
    public HashMap<String, String> encryptData(HashMap<String, String> params) {
        String json = new Gson().toJson(params);
        HashMap<String, String> param = new HashMap<>();
        try {
            //String enc = encryptMsg(json, appUtils.generateKey(Constants.SecretKey));
            //param.put("token", enc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }


    /*  *  * method to generate key to encrypt/decrypt server response  *  * */
    public SecretKey generateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(key.getBytes(), "AES");
    }


    /*
      *  *
      *  * method to encrypt server request
      *
      *  * */
    public String encryptMsg(String message, SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
        if (secretKey != null) {
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte iv[] = Constants.initVector.getBytes();
            //  new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
            return Base64.encodeToString(cipherText, Base64.NO_WRAP);
        }
        return "";
    }


    /*
     * method to decrypt encrypted server response
     *
     */
    public String decryptMsg(byte[] cipherText, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        if (secretKey != null) {
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte iv[] = Constants.initVector.getBytes();
            //  new SecureRandom().nextBytes(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] ciphertextBytes = Base64.decode(cipherText, Base64.NO_WRAP);
            // String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
            byte[] original = cipher.doFinal(ciphertextBytes);
            String decryptString = new String(original, "UTF-8");

            return decryptString;
        }
        return "";
    }


    /**
     * method to track screen
     *
     * @param screenName
     */
    public void trackScreen(String screenName,Activity context) {
        // Obtain the shared Tracker instance.

        BonAppApplication application = (BonAppApplication) context.getApplication();
        mTracker = application.getDefaultTracker();

        Log.i("Track", "Setting screen name: " + screenName);
        mTracker.setScreenName("Screen~" + screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * method to send event
     *
     * @param iCategoryId identification of click
     * @param iActionId   name of action performed
     * @param iLabelId    label of button clicked
     */
    public void SendEventGoogleAnalytics(Activity context
                                         ,String iCategoryId, String iActionId, String iLabelId, long value) {
        // Build and send an Event.
        BonAppApplication application = (BonAppApplication) context.getApplication();
        mTracker = application.getDefaultTracker();


        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(iCategoryId)
                .setAction(iActionId)
                .setLabel(iLabelId)
                .setValue(value)
                .build());

    }

    public void notifyDataSetChangedWithAnim(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
