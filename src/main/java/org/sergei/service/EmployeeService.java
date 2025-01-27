package org.sergei.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.sergei.entity.Employee;


import java.util.List;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
public class EmployeeService {

    @PersistenceContext(unitName ="EmployeePU")
    private EntityManager entityManager;


    @POST
    @Path("add")
    public Response registerEmployee(Employee employee) {
        entityManager.merge(employee);
        return Response.ok(employee).build();
    }

    @POST
    @Path("/add-native")
    public Response addEmployeeNative(@QueryParam("name") String name, @QueryParam("salary") double salary) {
        try {
            entityManager.createNativeQuery("INSERT INTO employee (name, salary) VALUES ( :name, :salary)")
                    .setParameter("name", name)
                    .setParameter("salary", salary)
                    .executeUpdate();
            return Response.ok("Employee added successfully").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error adding employee: " + e.getMessage())
                    .build();
        }
    }



    @GET
    @Path("/getEmployees")
    public List<Employee> getAllEmployees() {
        return entityManager.createQuery("SELECT e FROM employee e", Employee.class).getResultList();
    }

    @POST
    @Path("/addEmployees")
    public Response addEmployees(List<Employee> employees) {
        for (Employee employee : employees) {
            entityManager.persist(employee);
        }
        return Response.ok(employees).build();
    }
}