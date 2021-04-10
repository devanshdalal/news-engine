import React from "react";
import { AutoSizer, WindowScroller, List } from "react-virtualized";

import MatchCard from "./MatchCard";

class MatchesList extends React.Component {
  constructor(props) {
    super(props);
    const likes = localStorage.getItem("likes");
    this.state = {
      ids: likes ? JSON.parse(likes) : []
    };
    console.log('likes', likes);
  }

  render() {
    const { matches } = this.props;
    return (
      <div className="MatchesList">
        <div className="app-container">
          <WindowScroller>
            {({
              height,
              isScrolling,
              registerChild,
              onChildScroll,
              scrollTop
            }) => (
              <div className="WindowScroller">
                <AutoSizer disableHeight>
                  {({ width }) => (
                    <div ref={registerChild}>
                      <List
                        autoHeight
                        height={height}
                        width={width}
                        scrollTop={scrollTop}
                        isScrolling={isScrolling}
                        onScroll={onChildScroll}
                        overscanRowCount={4}
                        rowCount={matches.length}
                        rowHeight={180}
                        rowRenderer={({
                          index,
                          isScrolling,
                          isVisible,
                          key,
                          style
                        }) => {
                          return (
                            <MatchCard
                              ids={this.state.ids}
                              item={matches[index]}
                              keyValue={key}
                              {...{
                                index,
                                isScrolling,
                                key,
                                isVisible,
                                style
                              }}
                            />
                          );
                        }}
                      />
                    </div>
                  )}
                </AutoSizer>
              </div>
            )}
          </WindowScroller>
        </div>
      </div>
    );
  }
}

export default MatchesList;
