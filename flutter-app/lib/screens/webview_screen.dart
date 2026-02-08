import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'error_screen.dart';

class WebViewScreen extends StatefulWidget {
  const WebViewScreen({super.key});

  @override
  State<WebViewScreen> createState() => _WebViewScreenState();
}

class _WebViewScreenState extends State<WebViewScreen> {
  late final WebViewController _controller;
  bool _isLoading = true;
  bool _hasError = false;
  String _errorMessage = '';

  // 사용자 웹 URL (환경에 따라 변경 필요)
  static const String _webUrl = String.fromEnvironment(
    'WEB_URL',
  );

  @override
  void initState() {
    super.initState();
    _initializeWebView();
    _checkConnectivity();
  }

  // 앱 시작 시 저장된 쿠키 복원
  Future<void> _restoreCookies() async {
    final prefs = await SharedPreferences.getInstance();
    final cookies = prefs.getString('webview_cookies');
    if (cookies != null && cookies.isNotEmpty) {
      await _controller.runJavaScript('document.cookie = "$cookies"');
    }
  }

  // 쿠키 저장
  Future<void> _saveCookies() async {
    try {
      final cookies = await _controller.runJavaScriptReturningResult('document.cookie') as String;
      if (cookies.isNotEmpty) {
        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('webview_cookies', cookies);
      }
    } catch (e) {
      // 쿠키 저장 실패는 무시
    }
  }

  Future<void> _checkConnectivity() async {
    final connectivityResult = await Connectivity().checkConnectivity();
    if (!mounted) return;

    if (connectivityResult.contains(ConnectivityResult.none)) {
      setState(() {
        _hasError = true;
        _errorMessage = '인터넷 연결을 확인해주세요.';
        _isLoading = false;
      });
    }
  }

  Future<void> _initializeWebView() async {
    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setBackgroundColor(Colors.white)
      ..setNavigationDelegate(
        NavigationDelegate(
          onPageStarted: (String url) {
            if (mounted) {
              setState(() {
                _isLoading = true;
                _hasError = false;
              });
            }
          },
          onPageFinished: (String url) async {
            if (mounted) {
              setState(() {
                _isLoading = false;
              });
              // 로그인 페이지가 아닐 때만 쿠키 저장 (로그인 후)
              if (!url.contains('/login')) {
                await _saveCookies();
              }
            }
          },
          onWebResourceError: (WebResourceError error) {
            // 메인 프레임(페이지 자체)의 에러만 처리, 리소스(이미지, CSS 등) 에러는 무시
            if (error.isForMainFrame == true && mounted) {
              setState(() {
                _hasError = true;
                _errorMessage = _getErrorMessage(error);
                _isLoading = false;
              });
            }
          },
        ),
      )
      ..enableZoom(false);

    // 쿠키 관리자 설정 (로그인 상태 유지)
    if (Platform.isAndroid) {
      _configureAndroidWebView();
    }

    // 저장된 쿠키를 먼저 복원한 후 페이지 로드
    await _restoreCookies();
    await _controller.loadRequest(Uri.parse(_webUrl));
  }

  void _configureAndroidWebView() {
    // Android WebView 설정
  }

  String _getErrorMessage(WebResourceError error) {
    switch (error.errorType) {
      case WebResourceErrorType.hostLookup:
        return '서버를 찾을 수 없습니다.\n인터넷 연결을 확인해주세요.';
      case WebResourceErrorType.timeout:
        return '연결 시간이 초과되었습니다.\n다시 시도해주세요.';
      case WebResourceErrorType.connect:
        return '서버에 연결할 수 없습니다.\n네트워크를 확인해주세요.';
      case WebResourceErrorType.unsupportedScheme:
        return '지원하지 않는 URL입니다.';
      default:
        return '페이지를 로드할 수 없습니다.\n${error.description}';
    }
  }

  Future<bool> _onWillPop() async {
    if (await _controller.canGoBack()) {
      await _controller.goBack();
      return false;
    }
    return true;
  }

  void _reload() {
    setState(() {
      _hasError = false;
      _isLoading = true;
    });
    _controller.reload();
  }

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, result) async {
        if (didPop) return;
        final shouldPop = await _onWillPop();
        if (shouldPop) {
          // 앱 종료 (시스템 레벨)
          SystemNavigator.pop();
        }
      },
      child: Scaffold(
        backgroundColor: Colors.white,
        body: SafeArea(
          child: Stack(
            children: [
              // WebView
              if (!_hasError)
                WebViewWidget(controller: _controller),

              // Error Screen
              if (_hasError)
                ErrorScreen(
                  message: _errorMessage,
                  onRetry: _reload,
                ),

              // Loading Indicator
              if (_isLoading && !_hasError)
                Container(
                  color: Colors.white,
                  child: const Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        CircularProgressIndicator(
                          color: Color(0xFF6366F1),
                        ),
                        SizedBox(height: 16),
                        Text(
                          '페이지를 불러오는 중...',
                          style: TextStyle(
                            fontSize: 14,
                            color: Colors.grey,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}