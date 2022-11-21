package net.diyigemt.mpu.exception

/**
 *@Author hjn
 *@Create 2022/11/19
 */
class DuplicateIdException(
    private val id: String,
    override val message: String? = "Another plugin same artifact id named: $id found, skip"
): Throwable()
