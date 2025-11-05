package dev.jay.passwordmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.jay.passwordmanager.components.AccountNameTextInputCommon
import dev.jay.passwordmanager.components.BottomSheetCommon
import dev.jay.passwordmanager.components.DeleteButton
import dev.jay.passwordmanager.components.EditButton
import dev.jay.passwordmanager.components.InfoTextItem
import dev.jay.passwordmanager.components.PasswordListComponentItem
import dev.jay.passwordmanager.components.PasswordTextInputCommon
import dev.jay.passwordmanager.components.UserNameTextInputCommon
import dev.jay.passwordmanager.ui.theme.FabBg
import dev.jay.passwordmanager.ui.theme.GhostWhite
import dev.jay.passwordmanager.ui.theme.LightGray
import dev.jay.passwordmanager.ui.theme.PasswordManagerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: PasswordViewModel = viewModel()
            val state by vm.addState.collectAsState()

            var showBottomSheet by remember { mutableStateOf(false) }

            val screenSnackBarHostState = remember { SnackbarHostState() }
            val bottomSheetSnackBarHostState = remember { SnackbarHostState() }
            val detailSheetSnackBarHostState = remember { SnackbarHostState() }

            var snackBarMessage by remember { mutableStateOf<AppSnackBarMessage?>(null) }

            LaunchedEffect(Unit) {
                vm.events.collect { message ->
                    if (message.contains("Saved", ignoreCase = true) || message.contains(
                            "Updated",
                            ignoreCase = true
                        )
                    ) {
                        snackBarMessage = AppSnackBarMessage(message, SnackBarType.SUCCESS)
                        showBottomSheet = false
                        screenSnackBarHostState.showSnackbar(message)
                    } else {
                        snackBarMessage = AppSnackBarMessage(message, SnackBarType.ERROR)
                        bottomSheetSnackBarHostState.showSnackbar(message)
                    }
                }
            }

            val passwordList by vm.passwords.collectAsState()
            var showDetailsSheet by remember { mutableStateOf(false) }
            val detail by vm.detailState.collectAsState()

            val scope = rememberCoroutineScope()

            PasswordManagerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(
                            screenSnackBarHostState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(Alignment.Bottom)
                        ) { data ->

                            val type = snackBarMessage?.type ?: SnackBarType.SUCCESS

                            val bg = when (type) {
                                SnackBarType.SUCCESS -> FabBg
                                SnackBarType.ERROR,
                                SnackBarType.DELETE -> Color.Red
                            }

                            Snackbar(
                                containerColor = bg,
                                contentColor = Color.White,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(data.visuals.message, fontWeight = FontWeight.Medium)
                            }
                        }
                    },
                    topBar = {
                        Column {
                            TopAppBar(
                                title = {
                                    Text(
                                        "Password Manager",
                                        color = Color.Black,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = GhostWhite,
                                    titleContentColor = Color.Black
                                )
                            )

                            HorizontalDivider(
                                color = LightGray,
                                thickness = 1.dp
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                showBottomSheet = true
                            },
                            containerColor = FabBg,
                            shape = RoundedCornerShape(10.dp),
                            modifier = if (screenSnackBarHostState.currentSnackbarData != null) {
                                Modifier.windowInsetsPadding(WindowInsets.safeContent)
                            } else {
                                Modifier.padding(end = 22.dp, bottom = 22.dp)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add",
                                tint = Color.White
                            )
                        }
                    }) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(GhostWhite)
                    ) {
                        if (passwordList.isEmpty()) {
                            Text(
                                text = "No passwords yet.\nGo ahead and save something 😊",
                                color = Color.Gray,
                                modifier = Modifier
                                    .align(Alignment.Center),
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(passwordList) { item ->
                                    PasswordListComponentItem(
                                        title = item.accountName,
                                        onArrowClick = {
                                            vm.loadDetail(item.id)
                                            showDetailsSheet = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    BottomSheetCommon(
                        visible = showBottomSheet,
                        title = if (state.editingId == null) "Add New Password" else "Edit Password",
                        onDismiss = { showBottomSheet = false },
                        snackBarHostState = bottomSheetSnackBarHostState
                    ) {
                        AccountNameTextInputCommon(
                            value = state.accountName,
                            onValueChange = vm::updateAccountName,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(14.dp))
                        UserNameTextInputCommon(
                            value = state.userName,
                            onValueChange = vm::updateUserName,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(14.dp))
                        PasswordTextInputCommon(
                            value = state.password,
                            onValueChange = vm::updatePassword,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(28.dp))

                        EditButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = if (state.editingId == null) "Add new account" else "Update"
                        ) {
                            if (state.editingId == null) vm.save() else vm.update()
                        }

                        Spacer(Modifier.height(16.dp))
                    }

                    BottomSheetCommon(
                        visible = showDetailsSheet,
                        title = "Account Details",
                        onDismiss = { showDetailsSheet = false },
                        snackBarHostState = detailSheetSnackBarHostState
                    ) {

                        if (detail.loading) {
                            Text("Decrypting...", color = Color.Gray)
                            return@BottomSheetCommon
                        }

                        if (detail.error != null) {
                            Text(detail.error!!, color = Color.Red)
                            return@BottomSheetCommon
                        }

                        if (detail.accountName != null && detail.userName != null && detail.password != null) {

                            var showPassword by remember { mutableStateOf(false) }

                            InfoTextItem(
                                label = "Account Name",
                                value = detail.accountName!!
                            )
                            Spacer(Modifier.height(16.dp))

                            InfoTextItem(
                                label = "Username / Email",
                                value = detail.userName!!
                            )
                            Spacer(Modifier.height(16.dp))

                            InfoTextItem(
                                label = "Password",
                                value = if (showPassword) detail.password!! else "*******",
                                trailingIcon = if (showPassword) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                onIconClick = { showPassword = !showPassword }
                            )
                            Spacer(Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                EditButton(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp), text = "Edit",
                                    onClick = {
                                        vm.startEdit(
                                            detail.id!!,
                                            detail.accountName!!,
                                            detail.userName!!,
                                            detail.password!!
                                        )
                                        showDetailsSheet = false
                                        showBottomSheet = true
                                    }
                                )

                                DeleteButton(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                ) {
                                    vm.delete(detail.id!!)
                                    showDetailsSheet = false

                                    scope.launch {
                                        snackBarMessage = AppSnackBarMessage(
                                            "Password deleted",
                                            SnackBarType.DELETE
                                        )
                                        screenSnackBarHostState.showSnackbar(snackBarMessage!!.text)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

