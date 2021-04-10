import React from "react";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import {
  UncontrolledDropdown,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
  Navbar,
  Nav,
  NavbarBrand,
  NavbarToggler,
  Collapse,
  NavItem,
} from "reactstrap";
import { Link } from "react-router-dom";

import { InputGroup, InputGroupAddon, Button, Input } from "reactstrap";

import getImage from "utils/images";
import { itemsFilterChange, resetStore } from "redux/actions/getItem";

class Header extends React.Component {
  constructor(props) {
    super(props);
    this.state = { query: "", isOpen: false };
  }

  toggle = () => {
    this.setState({ isOpen: !this.state.isOpen });
  };

  adocSearch = () => {
    this.props.dispatch(
      itemsFilterChange({ search: this.state.query, skip: 0 })
    );
    this.props.dispatch(resetStore());
    this.props.history.push(`/search`);
  };

  resetHome = () => {
    this.props.dispatch(resetStore({}));
    this.props.history.push(`/home`);
  };

  reacordQuery = (query) => {
    this.setState({ query });
  };

  handleEnter = (event) => {
    if (event.key === "Enter") {
      this.adocSearch();
    }
  };

  render() {
    const isLoggedIn = localStorage.getItem("user");
    return (
      <Navbar light color="light" expand="md">
        <NavbarBrand onClick={() => this.resetHome()}>
          <img
            className="Navbar_Logo"
            src={getImage("logo")}
            alt="logo"
            width="35"
            height="35"
          />
        </NavbarBrand>
        <NavbarToggler onClick={this.toggle} />
        <Collapse isOpen={this.state.isOpen} navbar>
          <Nav className="nav-bar">
            <NavItem className="search-bar">
              <InputGroup className="search-bar-content">
                <Input
                  value={this.state.query}
                  onChange={(e) => this.reacordQuery(e.target.value)}
                  onKeyPress={this.handleEnter}
                />
                <InputGroupAddon addonType="append">
                  <Button color="primary" onClick={this.adocSearch}>
                    Search
                  </Button>
                </InputGroupAddon>
              </InputGroup>
            </NavItem>
            <NavItem className="nav-flushright">
              {isLoggedIn ? (
                <UncontrolledDropdown nav inNavbar>
                  <DropdownToggle caret nav>
                    Me
                  </DropdownToggle>
                  <DropdownMenu right>
                    <Link to="recommendations">
                      <DropdownItem> Recommendations </DropdownItem>{" "}
                    </Link>
                    <DropdownItem divider />
                    <Link to="liked">
                      <DropdownItem> Liked </DropdownItem>{" "}
                    </Link>
                    <DropdownItem divider />
                    <Link to="login">
                      <DropdownItem> Logout </DropdownItem>{" "}
                    </Link>
                  </DropdownMenu>
                </UncontrolledDropdown>
              ) : (
                <div>
                  <Link to="login"> Login </Link>
                </div>
              )}
            </NavItem>
          </Nav>
        </Collapse>
      </Navbar>
    );
  }
}
const mapStateToProps = (state) => {
  return {
    filters: state.itemsReducer.filters,
  };
};

const HeaderWithRouter = withRouter((props) => <Header {...props} />);
export default connect(mapStateToProps)(HeaderWithRouter);
