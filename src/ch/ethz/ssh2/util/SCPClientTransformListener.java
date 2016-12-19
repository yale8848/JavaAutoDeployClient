package ch.ethz.ssh2.util;

/**
 * Created by Yale on 2016/12/17.
 */
public interface SCPClientTransformListener {

    void transform(long uploadedSize,long total,int percent);
}
