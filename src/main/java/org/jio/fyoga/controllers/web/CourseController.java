package org.jio.fyoga.controllers.web;/*  Welcome to Jio word
    @author: Jio
    Date: 6/28/2023
    Time: 1:17 PM
    
    ProjectName: FYoGa
    Jio: I wish you always happy with coding <3
*/

import jakarta.servlet.http.HttpSession;
import org.jio.fyoga.entity.Course;
import org.jio.fyoga.entity.Discount;
import org.jio.fyoga.entity.Package;
import org.jio.fyoga.model.DiscountDTO;
import org.jio.fyoga.model.PackageDTO;
import org.jio.fyoga.service.ICourseService;
import org.jio.fyoga.service.IDiscountService;
import org.jio.fyoga.service.IPackageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/FYoGa/Course")
@Controller
public class CourseController {

    @Autowired
    ICourseService courseService;
    @Autowired
    IPackageService packageService;
    @Autowired
    IDiscountService discountService;

    // xu ly course trong
    @GetMapping("")
    public String showCourse(@RequestParam String name, HttpSession session, Model model) {
        //ArrayList<Course> COURSES = (ArrayList<Course>) session.getAttribute("COURSES");
        Course course = courseService.findCourseByCourse_name(name);

        String url = "redirect:/";
        if( course != null){
            model.addAttribute("COURSE",course);

            List<Package> packages = packageService.findAllByCourse_CourseID(course.getCourseID());
            //List<PackageDTO> packageDTOs = tranferDTO2Entity(packages);

            if (packages != null && packages.size() != 0){
                model.addAttribute("PACKAGES", packages);
            }
            else {
                model.addAttribute("MSG", "Chưa có gói nào cho khóa này. Xin vui lòng quay lại sau");
            }
            url = "web/course";
        }
        return url;
    }

    @GetMapping("/Package")
    public String showPackage(@RequestParam int courseID, @RequestParam int packageID, Model model){

        Optional<Package> pkg = packageService.findById(packageID);
        List<Discount> discounts = new ArrayList<>();


        if (pkg.isPresent()){
            Package pack = pkg.orElseThrow();

            model.addAttribute("PACKE",pkg );
        }



        for (Discount discount:discountService.findAllByAPackage_PackageID(packageID)) {
            discounts.add(discount);
        }


        List<DiscountDTO> discountDTOs = tranferDTO2Entity(discounts, pkg);
        Optional<Course> course =  courseService.findById(courseID);
        if ((course.isPresent())) {
            model.addAttribute("COURSE", course);
        }
        model.addAttribute("DISCOUNTS", discountDTOs);

        return "web/pakageCourse";
    }

    // qua han ko con su dung

//    public List<PackageDTO> tranferDTO2Entity(List<Package> packagesEntitys){
//        List<PackageDTO> packageDTOs = new ArrayList<>();
//
//        for (Package packageEntiry : packagesEntitys){
//            PackageDTO packageDTO = new PackageDTO();
//            BeanUtils.copyProperties(packageEntiry, packageDTO);
//            float priceDiscount = packageDTO.getPrice() * (100 - packageDTO.getPercentDiscount())/100 ;
//            packageDTO.setPriceDiscount(priceDiscount);
//            packageDTOs.add(packageDTO);
//
//        }
//
//        return packageDTOs;
//    }

    public List<DiscountDTO> tranferDTO2Entity(List<Discount> discountEntitys, Optional<Package> pkg ){
        List<DiscountDTO> DiscountDTOs = new ArrayList<>();

        for (Discount discountEntiry : discountEntitys){
            DiscountDTO discountDTO = new DiscountDTO();
            BeanUtils.copyProperties(discountEntiry, discountDTO);
            float priceDiscount = discountEntiry.getAPackage().getPrice() * (100 - discountDTO.getPercentDiscount())/100 ;
            discountDTO.setPriceDiscount(priceDiscount);
            discountDTO.setSlotOnMonth(pkg.get().getSlotOnMonth());
            DiscountDTOs.add(discountDTO);

        }

        return DiscountDTOs;
    }



}
