package mao.android_send_mms_with_fileprovider.entiey;

/**
 * Project name(项目名称)：android_send_MMS_with_FileProvider
 * Package(包名): mao.android_send_mms_with_fileprovider.entiey
 * Class(类名): ImageInfo
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/2
 * Time(创建时间)： 14:09
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class ImageInfo
{
    /**
     * id
     */
    private long id;
    /**
     * 名字
     */
    private String name;
    /**
     * 大小
     */
    private long size;
    /**
     * 路径
     */
    private String path;

    /**
     * Instantiates a new Image info.
     */
    public ImageInfo()
    {
    }

    /**
     * Instantiates a new Image info.
     *
     * @param id   the id
     * @param name the name
     * @param size the size
     * @param path the path
     */
    public ImageInfo(long id, String name, long size, String path)
    {
        this.id = id;
        this.name = name;
        this.size = size;
        this.path = path;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public ImageInfo setId(long id)
    {
        this.id = id;
        return this;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public ImageInfo setName(String name)
    {
        this.name = name;
        return this;
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public long getSize()
    {
        return size;
    }

    /**
     * Sets size.
     *
     * @param size the size
     * @return the size
     */
    public ImageInfo setSize(long size)
    {
        this.size = size;
        return this;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Sets path.
     *
     * @param path the path
     * @return the path
     */
    public ImageInfo setPath(String path)
    {
        this.path = path;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("id：").append(id).append('\n');
        stringbuilder.append("name：").append(name).append('\n');
        stringbuilder.append("size：").append(size).append('\n');
        stringbuilder.append("path：").append(path).append('\n');
        return stringbuilder.toString();
    }
}
