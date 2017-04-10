var Comments = React.createClass({
    loadComponentFromServer: function() {
        $.ajax({
            url: "/boards/" + this.props.boardSeq + "/comments"
            ,dataType: "json"
            ,cache: false
            ,success: function(comments) {
                this.setState({data: comments});
            }.bind(this)
            ,error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    handleDeleteComment: function(comment) {
        if (!confirm("삭제하시겠습니까?")) {
            return;
        }
        $.ajax({
            url: "/boards/" + this.props.boardSeq + "/comments" + "/" + comment.seq
            ,type: "delete"
            ,dataType: "json"
            ,cache: false
            ,success: function() {
                var comments = this.state.data;
                comments.splice(comments.indexOf(comment), 1);
                this.setState({data: comments});
            }.bind(this)
            ,error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    handleCreateComment: function(comment) {
        $.ajax({
            url: "/boards/" + this.props.boardSeq + "/comments"
            ,type: "post"
            ,dataType: "json"
            ,contentType: "application/json"
            ,data: JSON.stringify(comment)
            ,cache: false
            ,success: function(comment) {
                var comments = this.state.data;
                var newComments = comments.concat([comment]);
                this.setState({data: newComments});
            }.bind(this)
            ,error: function(xhr, status, err) {
                console.error(this.props.url, status, err.toString());
            }.bind(this)
        });
    },
    getInitialState: function () {
        return {data: []};
    },
    componentDidMount: function() {
        this.loadComponentFromServer();
    },
    render: function() {
        var handleDeleteComment = this.handleDeleteComment;
        var comments = this.state.data.map(function (comment) {
            return (
                <Comment comment={comment} key={comment.seq} onDeleteComment={handleDeleteComment}/>
            );
        });
        return (
            <div className="container">
                {comments}
                <CommentForm onCreateComment={this.handleCreateComment}/>
            </div>
        );
    }
});
var Comment = React.createClass({
    dateFormat: function (timeMillis) {
        var date = new Date(timeMillis);
        return date.dateFormat('yyyy.mm.dd hh:ii:ss');
    },
    handleDelete: function(e) {
        e.preventDefault();
        this.props.onDeleteComment(this.props.comment);
    },
    render: function () {
        return (
            <div className="panel panel-default">
                <div className="panel-heading">
                    {this.dateFormat(this.props.comment.regDate)} &nbsp; {this.props.comment.writer} &nbsp;
                    <button className="btn btn-danger btn-xs" onClick={this.handleDelete}>x</button>
                </div>
                <div className="panel-body">
                    {this.props.comment.content}
                </div>
            </div>
        );
    }
});
var CommentForm = React.createClass({
    handleCreate: function(e) {
        var getValueAndCheck = function(ref, msg) {
            var element = ReactDOM.findDOMNode(ref);
            var value = element.value.trim();
            if (value === "") {
                alert(msg);
                element.focus();
            }
            return value;
        };
        var setEmptyValue = function(ref) {
            ReactDOM.findDOMNode(ref).value = "";
        };

        e.preventDefault();
        var writer = getValueAndCheck(this.refs.writer, "작성자를 입력해주세요.");
        if (writer === "") {
            return;
        }
        var content = getValueAndCheck(this.refs.content, "내용을 입력해주세요.");
        if (content === "") {
            return;
        }
        this.props.onCreateComment({writer: writer, content: content});
        setEmptyValue(this.refs.writer);
        setEmptyValue(this.refs.content);
    },
    render: function() {
        return (
            <form onSubmit={this.handleCreate}>
                <div className="panel panel-info">
                    <div className="panel-heading">
                        <input type="text" name="writer" className="form-control" ref="writer" placeholder="작성자"/>
                    </div>
                    <div className="panel-body">
                        <textarea className="form-control" name="content" ref="content" placeholder="댓글내용"></textarea>
                        <p/>
                        <div className="btn-group pull-right">
                            <button type="submit" className="btn btn-primary">등록</button>
                        </div>
                    </div>
                </div>
            </form>
        );
    }
});
ReactDOM.render(
    <Comments boardSeq={window._initParam.boardSeq}/>,
    document.getElementById("comments")
);