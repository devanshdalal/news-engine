import React from "react";
import { connect } from "react-redux";
import InfiniteScroll from "react-infinite-scroller";
import { FaSearchMinus } from "react-icons/fa";

import { getItems, resetStore } from "redux/actions/getItem";
import MatchesList from "components/MatchesList";
import LoadingSpinner from "components/LoadingSpinner";

class Home extends React.Component {
  constructor(props) {
    super(props);
    // console.log('Calling constructor', props)
    props.dispatch(resetStore({}));
  }

  componentDidMount() {}

  loadMore = () => {
    const { dispatch, itemsApiInProgress, type } = this.props;
    // console.log('source', type)
    !itemsApiInProgress && dispatch(getItems(type));
  };

  render() {
    let {
      items,
      itemsApiInError,
      filters,
      totalItemCount,
      itemsApiInProgress,
    } = this.props;
    let { skip } = filters;
    return (
      <div className="Home">
        <>
          <div className="center Home_body_container">
            <InfiniteScroll
              pageStart={0}
              loadMore={this.loadMore}
              style={{ width: "100%" }}
              hasMore={!itemsApiInError && skip <= totalItemCount}
              loader={<div key={"loading"}></div>}
            >
              {items.length ? (
                <MatchesList matches={items} />
              ) : itemsApiInProgress ? (
                <div className="Home_body_container_ItemsApiInProgress">
                  <LoadingSpinner />
                </div>
              ) : (
                <div className="Home_noResult">
                  <FaSearchMinus size={60} />
                  <h4 className="title">No result found</h4>
                </div>
              )}
            </InfiniteScroll>
          </div>
        </>
      </div>
    );
  }
}
const mapStateToProps = (state) => {
  return {
    source: state.itemsReducer.source,
    loading: state.loadingReducer.loadState,
    items: state.itemsReducer.items,
    itemsApiInProgress: state.itemsReducer.itemsApiInProgress,
    itemsApiInError: state.itemsReducer.itemsApiInError,
    totalItemCount: state.itemsReducer.totalItemCount,
    filters: state.itemsReducer.filters,
  };
};
export default connect(mapStateToProps)(Home);
