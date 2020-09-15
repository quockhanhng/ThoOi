package com.sunasterisk.thooi.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.sunasterisk.thooi.data.repository.*
import com.sunasterisk.thooi.data.source.CategoryDataSource
import com.sunasterisk.thooi.data.source.NotificationDataSource
import com.sunasterisk.thooi.data.source.PostDataSource
import com.sunasterisk.thooi.data.source.UserDataSource
import com.sunasterisk.thooi.data.source.entity.UserType
import com.sunasterisk.thooi.data.source.local.LocalPostDataSource
import com.sunasterisk.thooi.data.source.local.NotificationLocalDataSource
import com.sunasterisk.thooi.data.source.local.UserLocalDataSource
import com.sunasterisk.thooi.data.source.local.database.AppDataBase
import com.sunasterisk.thooi.data.source.local.database.DatabaseConstants
import com.sunasterisk.thooi.data.source.remote.CategoryRemoteDataSource
import com.sunasterisk.thooi.data.source.remote.NotificationRemoteDataSource
import com.sunasterisk.thooi.data.source.remote.PostRemoteDataSource
import com.sunasterisk.thooi.data.source.remote.UserRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val repositoryModule = module {

    //Firebase modules
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseInstanceId.getInstance() }
    single { FirebaseStorage.getInstance() }

    //Room database modules
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDataBase::class.java,
            DatabaseConstants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    //Room Dao modules
    single { get<AppDataBase>().userDao() }

    //DataSource modules
    single<UserDataSource.Remote> { UserRemoteDataSource(get(), get()) }
    single<UserDataSource.Local> { UserLocalDataSource(get(), get()) }
    single<NotificationDataSource.Remote> { NotificationRemoteDataSource(get(), get()) }
    single<NotificationDataSource.Local> { NotificationLocalDataSource() } //FIXME: Not implemented yet

    //Repository modules
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get(), get()) }

    single<FirestoreRepository> { FirestoreRepositoryImpl(get()) }

    // CongVC
    single<PostDataSource.Remote> { PostRemoteDataSource(get()) }
    single<PostDataSource.Local> { LocalPostDataSource(get()) }
    single<PostRepository> { PostRepositoryImpl(get(), get()) }
    single<PostDetailRepository> { PostDetailRepositoryImpl(get(), get()) }
    single<CategoryDataSource.Remote> { CategoryRemoteDataSource(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single { UserType.CUSTOMER }
}
