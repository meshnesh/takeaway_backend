package com.juice.notification.takeawaybackend.controller

import com.juice.notification.takeawaybackend.data.dao.MenuItemDao
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class MenuItemController(private val dao: MenuItemDao) {
    @RequestMapping("/menu-items")
    fun getMenuItems() = dao.listMenuItems()
}
