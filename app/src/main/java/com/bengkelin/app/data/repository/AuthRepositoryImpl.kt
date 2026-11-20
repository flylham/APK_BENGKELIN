package com.bengkelin.app.data.repository

import com.bengkelin.app.BengkelinApp
import com.bengkelin.app.data.local.TokenManager
import com.bengkelin.app.data.local.UserDao
import com.bengkelin.app.data.model.MalangRegion
import com.bengkelin.app.data.model.Resource
import com.bengkelin.app.data.model.User
import com.bengkelin.app.data.model.UserRole
import com.bengkelin.app.data.remote.ApiClient
import com.bengkelin.app.data.remote.AuthApiService
import com.bengkelin.app.data.remote.dto.ApiErrorResponse
import com.bengkelin.app.data.remote.dto.LoginRequest
import com.bengkelin.app.data.remote.dto.RegisterRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val apiService: AuthApiService = ApiClient.authService,
    private val userDao: UserDao = BengkelinApp.instance.database.userDao(),
    private val tokenManager: TokenManager = BengkelinApp.instance.tokenManager
) : AuthRepository {

    override val activeUserFlow: Flow<User?>
        get() = userDao.getActiveUserFlow()

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        role: UserRole,
        region: MalangRegion,
        workshopName: String?
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val request = RegisterRequest(
                name = name.trim(),
                email = email.trim().lowercase(),
                password = password,
                phoneNumber = phoneNumber.trim(),
                role = role.roleKey,
                region = region.regionName,
                workshopName = if (role == UserRole.PEMILIK_BENGKEL) workshopName?.trim() else null
            )

            val response = apiService.register(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val userDto = body.user ?: throw Exception("Data profil pengguna kosong.")
                val token = body.token ?: ""

                val user = userDto.toUser(token)

                // Simpan ke database lokal Room SQLite
                userDao.saveUser(user)

                // Simpan token JWT ke DataStore
                tokenManager.saveAuthSession(token, user.id, user.role)

                emit(Resource.Success(user))
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            // Mode Offline Cerdas: Jika server belum dinyalakan, fallback ke Room lokal agar bisa langsung diuji
            if (e.message?.contains("Failed to connect", ignoreCase = true) == true ||
                e.message?.contains("Connection refused", ignoreCase = true) == true) {
                val offlineUser = User(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    phoneNumber = phoneNumber.trim(),
                    role = role.name,
                    region = region.regionName,
                    workshopName = if (role == UserRole.PEMILIK_BENGKEL) workshopName?.trim() else null,
                    isVerified = true,
                    token = "offline-demo-token"
                )
                userDao.saveUser(offlineUser)
                tokenManager.saveAuthSession("offline-demo-token", offlineUser.id, offlineUser.role)
                emit(Resource.Success(offlineUser))
            } else {
                emit(Resource.Error(parseNetworkException(e)))
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val request = LoginRequest(
                email = email.trim().lowercase(),
                password = password
            )

            val response = apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val userDto = body.user ?: throw Exception("Data profil pengguna kosong.")
                val token = body.token ?: ""

                val user = userDto.toUser(token)

                // Simpan ke Room SQLite
                userDao.saveUser(user)

                // Simpan sesi DataStore
                tokenManager.saveAuthSession(token, user.id, user.role)

                emit(Resource.Success(user))
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string())
                emit(Resource.Error(errorMsg))
            }
        } catch (e: Exception) {
            // Mode Offline Cerdas: Jika server belum dinyalakan, izinkan login offline ke Room
            if (e.message?.contains("Failed to connect", ignoreCase = true) == true ||
                e.message?.contains("Connection refused", ignoreCase = true) == true) {
                val offlineUser = User(
                    id = "offline-demo-id",
                    name = "Pengendara Malang (Mode Offline)",
                    email = email.trim().lowercase(),
                    phoneNumber = "08123456789",
                    role = UserRole.PENGGUNA.name,
                    region = "Kota Malang",
                    workshopName = null,
                    isVerified = true,
                    token = "offline-demo-token"
                )
                userDao.saveUser(offlineUser)
                tokenManager.saveAuthSession("offline-demo-token", offlineUser.id, offlineUser.role)
                emit(Resource.Success(offlineUser))
            } else {
                emit(Resource.Error(parseNetworkException(e)))
            }
        }
    }

    override suspend fun getCachedUser(): User? {
        return userDao.getActiveUser()
    }

    override suspend fun logout() {
        try {
            userDao.clearUsers()
            tokenManager.clearAuthSession()
        } catch (e: Exception) {
            // Abaikan error saat clear cache
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            if (!errorBody.isNullOrBlank()) {
                val errorObj = Gson().fromJson(errorBody, ApiErrorResponse::class.java)
                errorObj.message
            } else {
                "Permintaan gagal diproses oleh server."
            }
        } catch (e: Exception) {
            "Terjadi kesalahan pada respon server database."
        }
    }

    private fun parseNetworkException(e: Exception): String {
        val msg = e.message ?: ""
        return when {
            msg.contains("Failed to connect", ignoreCase = true) ||
            msg.contains("Connection refused", ignoreCase = true) ->
                "Tidak dapat terhubung ke server database backend. Pastikan server API berjalan di port 5000."
            msg.contains("timeout", ignoreCase = true) ->
                "Koneksi timeout. Server database merespon terlalu lama."
            else -> msg.ifBlank { "Terjadi gangguan jaringan." }
        }
    }
}
