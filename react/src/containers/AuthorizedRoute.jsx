// This is used to determine if a user is authenticated and
// if they are allowed to visit the page they navigated to.

// If they are: they proceed to the page
// If not: they are redirected to the login page.
import React from "react";
import { Redirect, Route } from "react-router-dom";

const AuthorizedRoute = ({ component: Component, ...rest }) => {
  // Add your own authentication on the below line.
  const isLoggedIn = localStorage.getItem("user") ? true : false;

  // console.log('rest', rest, isLoggedIn)

  return (
    <Route
      {...rest}
      render={props =>
        isLoggedIn ? (
          <div>
            {/* <div>{console.log(props, rest.type)}</div> */}
            <Component type={rest.type} {...props} />
          </div>
        ) : (
          <Redirect to={{ pathname: "/login" }} />
        )
      }
    />
  );
};

export default AuthorizedRoute;
