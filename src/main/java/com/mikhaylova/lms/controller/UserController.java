package com.mikhaylova.lms.controller;

import com.mikhaylova.lms.domain.Role;
import com.mikhaylova.lms.dto.UserDto;
import com.mikhaylova.lms.exception.InternalServerError;
import com.mikhaylova.lms.exception.NotFoundException;
import com.mikhaylova.lms.service.AvatarStorageService;
import com.mikhaylova.lms.service.CourseService;
import com.mikhaylova.lms.service.RoleService;
import com.mikhaylova.lms.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final CourseService courseService;
    private AvatarStorageService avatarStorageService;

    public UserController(UserService userService,
                          RoleService roleService,
                          CourseService courseService, AvatarStorageService avatarStorageService) {
        this.userService = userService;
        this.roleService = roleService;
        this.courseService = courseService;
        this.avatarStorageService = avatarStorageService;
    }

    @ModelAttribute("roles")
    public List<Role> rolesAttribute() {
        return roleService.findAll();
    }

    @ModelAttribute("principalId")
    public Long principalIdAttribute(Principal principal) {
        if (principal != null)
            return userService.findUserByUsername(principal.getName()).getId();
        else
            return null;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String userForm(Model model,
                           @PathVariable("id") Long id,
                           Principal principal,
                           HttpServletRequest request) {
        UserDto userDto = userService.findUserDtoById(id);
        if (!principal.getName().equals(userDto.getUsername()) && !request.isUserInRole("ROLE_ADMIN"))
            throw new AccessDeniedException("Access denied");
        if (principal.getName().equals(userDto.getUsername()))
            model.addAttribute("activePage", "user");
        else
            model.addAttribute("activePage", "users");
        model.addAttribute("user", userDto);
        return "user-form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public String submitUserForm(@Valid @ModelAttribute("user") UserDto user,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "user");
            return "user-form";
        }
        //не администратор не должен иметь доступа к ролям
        if (!request.isUserInRole("ROLE_ADMIN"))
            user.setRoles(null);
        userService.saveUserDto(user);
        return "redirect:/course";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/users")
    public String usersTable(Model model) {
        model.addAttribute("users", userService.findAllUserDto());
        model.addAttribute("activePage", "users");
        return "users-table";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/avatar")
    public String updateAvatarImage(@RequestParam("avatar") MultipartFile avatar,
                                    @PathVariable("id") Long id) {
        try {
            avatarStorageService.save(id, avatar.getContentType(), avatar.getInputStream());
        } catch (Exception ex) {
            throw new InternalServerError("Internal server error happened during file saving.");
        }
        return "redirect:/user/" + id;
    }

    //перенаправление в личный кабинет
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String getUserProfile(HttpServletRequest request) {
        Long id = userService.findUserByUsername(request.getUserPrincipal().getName()).getId();
        return "redirect:/user/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/avatar")
    @ResponseBody
    public ResponseEntity<byte[]> avatarImageByUserId(@PathVariable("id") Long id) {
        String contentType = avatarStorageService.getContentTypeByUserId(id);
        byte[] data = avatarStorageService.getAvatarImageByUserId(id)
                .orElseThrow(NotFoundException::new);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    //для получение аватара текущего пользователя в nav-bar, где id пользователя неизвестен
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/avatar")
    @ResponseBody
    public ResponseEntity<byte[]> avatarImageOfCurrentUser(Authentication auth) {
        return avatarImageByUserId(userService.findUserByUsername(auth.getName()).getId());
    }

    @DeleteMapping("/unassign/{courseId}")
    public String unassignUser(@PathVariable("courseId") Long courseId, @RequestParam("userId") Long userId) {
        courseService.unassignUser(courseId, userId);
        return "redirect:/user/" + userId;
    }

}
