## 介绍

1. 可在文件浏览器中选择指定后缀名文件
2. 可通过扫描全盘的方式，选择指定后缀名的文件
3. 类似微信图片选择器选择图片或视频
4. 图片选择页面可以自定义主题
5. 支持Activity、Fragment

## Example

![FilePicker](https://github.com/imLibo/FilePicker/blob/master/screenshot/Screenshot_20180307-124655.png)  ![FilePicker](https://github.com/imLibo/FilePicker/blob/master/screenshot/Screenshot_2018-03-07-13-51.png)  ![FilePicker](https://github.com/imLibo/FilePicker/blob/master/screenshot/Screenshot_20180307-124316.png)  
![FilePicker](https://github.com/imLibo/FilePicker/blob/master/screenshot/Screenshot_20180307-124556.png)  ![FilePicker](https://github.com/imLibo/FilePicker/blob/master/screenshot/Screenshot_20180307-124202.png)  ![FilePicker](https://github.com/imLibo/FilePicker/blob/master/screenshot/Screenshot_20180307-124213.png)

---

### 可下载APK直接体验
++[Demo.apk](https://github.com/imLibo/FilePicker/blob/master/APK/release/filepicker-demo.apk)++


---

### 用法

##### 一、 在文件浏览器中选择指定文件
```
FilePicker.from(this)
                .chooseForBrowser()
                .setMaxCount(2)
                .setFileTypes("png", "doc","apk", "mp3", "gif", "txt", "mp4", "zip")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
```

##### 二、 分类查找指定后缀名文件

```
FilePicker
                .from(this)
                .chooseForMimeType()
                .setMaxCount(10)
                .setFileTypes("png", "doc","apk", "mp3", "gif", "txt", "mp4", "zip")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
```
##### 三、 在图片选择器中选择图片或视频

```
FilePicker
                .from(this)
                .chooseMedia()
                .enabledCapture(true)
                .setTheme(R.style.FilePicker_Dracula)
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
```

##### 四、 接收返回的文件数据，在 ++onActivityResult++ 方法中获取。选中文件以链表方式返回， ++EssFile++ 类为载体


```
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHOOSE) {
            ArrayList<EssFile> essFileList = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);
            StringBuilder builder = new StringBuilder();
            for (EssFile file :
                    essFileList) {
                builder.append(file.getMimeType()).append(" | ").append(file.getName()).append("\n\n");
            }
            textView.setText(builder.toString());
        }
    }
```



### 属性列表

名称 | 描述 |  默认值
---|---|---
FileTypes | 需要显示的文件类型 | 无
SortType | 排序类型 | 按名字排序 BY_NAME_ASC
isSingle | 是否单选 |false
maxCount | 最大可选中数量 | 10
request_code | 请求码 | 无
onlyShowImages | 是否仅显示图片（仅当其实chooseMedia时有效） | false
onlyShowVideos | 是否仅显示视频（仅当其实chooseMedia时有效） | false
enabledCapture | chooseMedia时是否显示照相机 | false
placeHolder | 图片的PlaceHolder | png_holder
themeId | 主题ID | R.Style.FilePicker_Elec

### 自定义主题


```
<style name="FilePicker.Elec" parent="Theme.AppCompat.Light.NoActionBar">
		<item name="colorPrimary">@color/elec_primary</item>
		<item name="colorPrimaryDark">@color/elec_primary_dark</item>
		<item name="toolbar">@style/Toolbar.elec</item>
		<item name="album.dropdown.title.color">@color/elec_album_dropdown_title_text</item>
		<item name="album.dropdown.count.color">@color/elec_album_dropdown_count_text</item>
		<item name="album.element.color">@android:color/white</item>
		<item name="album.thumbnail.placeholder">@color/elec_album_dropdown_thumbnail_placeholder</item>
		<item name="album.emptyView">@drawable/ic_empty_elec</item>
		<item name="album.emptyView.textColor">@color/elec_album_empty_view</item>
		<item name="item.placeholder">@color/elec_item_placeholder</item>
		<item name="item.checkCircle.backgroundColor">@color/elec_item_checkCircle_backgroundColor</item>
		<item name="item.checkCircle.borderColor">@color/elec_item_checkCircle_borderColor</item>
		<item name="page.bg">@color/elec_page_bg</item>
		<item name="bottomToolbar.bg">@color/elec_bottom_toolbar_bg</item>
		<item name="bottomToolbar.preview.textColor">@color/elec_bottom_toolbar_preview</item>
		<item name="bottomToolbar.apply.textColor">@color/elec_bottom_toolbar_apply</item>
		<item name="preview.bottomToolbar.back.textColor">@color/elec_preview_bottom_toolbar_back_text</item>
		<item name="preview.bottomToolbar.apply.textColor">@color/elec_preview_bottom_toolbar_apply</item>
		<item name="listPopupWindowStyle">@style/Popup.elec</item>
		<item name="capture.textColor">@color/elec_capture</item>
	</style>
```


### Feature TODO

- [ ] 根据文件类型打开/预览文件
- [ ] 预览图片界面
- [ ] 压缩图片
- [ ] 裁剪图片
- [ ] 自定义相机拍照
- [ ] 去掉AndPermission依赖，用原生代码申请权限
- [ ] 增加多种图片加载框架支持
- [ ] 文件浏览器支持自定义主题
- [ ] 分类选择文件界面支持自定义主题


### THANKS

[Android-FilePicker](https://github.com/DroidNinja/Android-FilePicker)

[Matisse](https://github.com/zhihu/Matisse)

[AndroidPicker](https://github.com/gzu-liyujiang/AndroidPicker)