package lib.net.udp

import java.net.InetAddress

data class User(val address: InetAddress, val port: Int, var userId: Int? = null)
