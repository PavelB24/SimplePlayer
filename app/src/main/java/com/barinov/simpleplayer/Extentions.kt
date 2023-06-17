package com.barinov.simpleplayer

import com.barinov.simpleplayer.domain.CoroutineFileWorker
import com.barinov.simpleplayer.domain.MusicFileIterator
import com.barinov.simpleplayer.domain.MusicFileIteratorImpl
import com.barinov.simpleplayer.domain.model.CommonFileItem
import com.barinov.simpleplayer.domain.model.MusicFile
import me.jahnen.libaums.core.fs.FileSystem
import me.jahnen.libaums.core.fs.UsbFile
import java.io.File
import java.io.InputStream
import java.io.OutputStream


fun List<MusicFile>.musicFileIterator(): MusicFileIterator {
    return MusicFileIteratorImpl(this)
}

inline fun <T> List<T>.indexOrNull(predicate: (T) -> Boolean): Int? {
    val iterator = this.listIterator()
    while (iterator.hasNext()) {
        if (predicate(iterator.next())) {
            return iterator.previousIndex()
        }
    }
    return null
}

fun File.toCommonFileItem() =
    CommonFileItem(CoroutineFileWorker.RootType.INTERNAL, this, null)

fun UsbFile.toCommonFileItem(fs: FileSystem) =
    CommonFileItem(CoroutineFileWorker.RootType.USB, null, CommonFileItem.UsbData(this, fs))
suspend inline fun InputStream.copyWithCallBack(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    onBlockCopied: suspend (Long) -> Unit
): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        onBlockCopied.invoke(bytesCopied)
        bytes = read(buffer)
    }
    return bytesCopied
}